document.addEventListener('DOMContentLoaded', function(){
    const fighter1Select = document.getElementById('fighter1');
    const fighter2Select = document.getElementById('fighter2');
    const fighter1Img = document.getElementById('fighter1-img');
    const fighter2Img = document.getElementById('fighter2-img');
    const announceFightBtn = document.getElementById('announceFightBtn');

    let fightersData = [];
    let eventsData = [];

    fetch('/api/mma-fighters/all')
        .then(response => response.json())
        .then(fighters =>
        {
            fightersData = fighters;
            fighters.forEach(f =>
            {
                const option1 = document.createElement('option');
                option1.value = f.fighterId;
                option1.text = f.first_name + ' ' + f.last_name;
                fighter1Select.appendChild(option1);

                const option2 = document.createElement('option');
                option2.value = f.fighterId;
                option2.text = f.first_name + ' ' + f.last_name;
                fighter2Select.appendChild(option2);
            });
        })
        .catch(error => console.error('Error fetching fighters:', error));

    fetch('/api/events/all')
        .then(response => response.json())
        .then(events => {eventsData = events;})
        .catch(error => console.error('Error fetching events:', error));

    fighter1Select.addEventListener('change', function(){
        const fighterId = fighter1Select.value;
        if(fighterId)
        {
            fighter1Img.src = '/css_js/images/' + fighterId + '.png';
            fighter1Img.style.display = 'block';
        }
        else fighter1Img.style.display = 'none';
    });

    fighter2Select.addEventListener('change', function(){
        const fighterId = fighter2Select.value;
        if(fighterId)
        {
            fighter2Img.src = '/css_js/images/' + fighterId + '.png';
            fighter2Img.style.display = 'block';
        }
        else fighter2Img.style.display = 'none';
    });

    announceFightBtn.addEventListener('click', function(){
        const f1Id = fighter1Select.value;
        const f2Id = fighter2Select.value;

        if(!f1Id || !f2Id) {alert("Please select both fighters!"); return;}

        const f1 = fightersData.find(x => x.fighterId == f1Id);
        const f2 = fightersData.find(x => x.fighterId == f2Id);

        if(!f1 || !f2) {alert("Could not find fighter data!"); return;}
        if(f1.weight_category_id.name !== f2.weight_category_id.name)
        {
            alert("These fighters are not in the same weight category! Cannot announce fight.");
            return;
        }

        const modalFighter1Name = document.getElementById('modal-fighter1-name');
        const modalFighter2Name = document.getElementById('modal-fighter2-name');
        const modalWeightCategory = document.getElementById('modal-weight-category');
        const modalEventSelect = document.getElementById('modal-event-select');
        const modalFightDate = document.getElementById('modal-fight-date');
        const modalEventTimes = document.getElementById('modal-event-times');

        modalFighter1Name.value = f1.first_name + ' ' + f1.last_name;
        modalFighter2Name.value = f2.first_name + ' ' + f2.last_name;
        modalWeightCategory.value = f1.weight_category_id.name;

        modalEventSelect.innerHTML = '<option value="">-- Select Event --</option>';
        eventsData.forEach(evt =>
        {
            const opt = document.createElement('option');
            opt.value = evt.eventId;
            opt.text = evt.eventName + ' (' + evt.location + ')';
            opt.setAttribute('data-start', evt.startOfEvent);
            opt.setAttribute('data-end', evt.endOfEvent);
            modalEventSelect.appendChild(opt);
        });

        modalEventTimes.innerText = '';
        $('#newFightModal').modal('show');
    });

    const modalEventSelect = document.getElementById('modal-event-select');
    const modalEventTimes = document.getElementById('modal-event-times');

    modalEventSelect.addEventListener('change', function(){
        const eventId = modalEventSelect.value;
        if(!eventId) {modalEventTimes.innerText = ''; return;}

        const selectedOpt = modalEventSelect.querySelector('option[value="'+eventId+'"]');
        const startOfEvent = new Date(selectedOpt.getAttribute('data-start'));
        const endOfEvent = new Date(selectedOpt.getAttribute('data-end'));

        const options = {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute:'2-digit'};
        const formattedStart = startOfEvent.toLocaleDateString(undefined, options);
        const formattedEnd = endOfEvent.toLocaleDateString(undefined, options);
        modalEventTimes.innerText = formattedStart + ' - ' + formattedEnd;
    });

    const newFightForm = document.getElementById('newFightForm');
    newFightForm.addEventListener('submit', function(e){
        e.preventDefault();

        const f1Id = parseInt(document.getElementById('fighter1').value);
        const f2Id = parseInt(document.getElementById('fighter2').value);
        const fightDateRaw = document.getElementById('modal-fight-date').value;
        const eventId = parseInt(document.getElementById('modal-event-select').value);
        const result = document.getElementById('modal-result').value;
        const typeOfResult = document.getElementById('modal-typeOfResult').value;

        const selectedEvent = eventsData.find(evt => evt.eventId === eventId);
        if(!selectedEvent) {alert("Selected event not found!"); return;}

        const fightDate = new Date(fightDateRaw);
        const startOfEvent = new Date(selectedEvent.startOfEvent);
        const endOfEvent = new Date(selectedEvent.endOfEvent);

        if(fightDate < startOfEvent || fightDate > endOfEvent)
        {
            alert("Selected fight date/time is not within the event period!");
            return;
        }

        const year = fightDate.getFullYear();
        const month = String(fightDate.getMonth() + 1).padStart(2, '0');
        const day = String(fightDate.getDate()).padStart(2, '0');
        const hours = String(fightDate.getHours()).padStart(2, '0');
        const minutes = String(fightDate.getMinutes()).padStart(2, '0');
        const formattedFightDate = `${year}-${month}-${day} ${hours}:${minutes}:00`;
        const selectedWeightCategory = fightersData.find(x => x.fighterId === f1Id).weight_category_id;
        const weightCategoryId = selectedWeightCategory.weightCategoryId;

        const fightData = {
            date: formattedFightDate,
            result: result,
            type_of_result: typeOfResult,
            weight_category: {
                weightCategoryId: weightCategoryId
            },
            event: {
                eventId: eventId,
                eventName: selectedEvent.eventName
            },
            fighters: [
                {fighterId: f1Id, first_name: fightersData.find(x => x.fighterId === f1Id).first_name, last_name: fightersData.find(x => x.fighterId === f1Id).last_name, accepted: false},
                {fighterId: f2Id, first_name: fightersData.find(x => x.fighterId === f2Id).first_name, last_name: fightersData.find(x => x.fighterId === f2Id).last_name, accepted: false}
            ]
        };

        let existingFights = JSON.parse(localStorage.getItem('newPendingFights')) || [];
        existingFights.push(fightData);
        localStorage.setItem('newPendingFights', JSON.stringify(existingFights));

        fetch('/api/fights/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            },
            body: JSON.stringify([fightData])
        })
            .then(response =>
            {
                if(!response.ok) return response.text().then(text => { throw new Error(text); });
                return response.text();
            })
            .then(data =>
            {
                fetch('/api/fights/all')
                    .then(res => res.json())
                    .then(allFights =>
                    {
                        const foundFight = allFights.find(ff =>
                            ff.date === formattedFightDate &&
                            ff.weight_category.name === fightersData.find(x => x.fighterId === f1Id).weight_category_id.name &&
                            ff.event.eventId === eventId
                        );
                        if(foundFight)
                        {
                            let updatedFights = JSON.parse(localStorage.getItem('newPendingFights')) || [];
                            updatedFights = updatedFights.map(f =>
                            {
                                if(f.date === fightData.date && f.event.eventId === fightData.event.eventId) f.fightId = foundFight.fightId;
                                return f;
                            });
                            localStorage.setItem('newPendingFights', JSON.stringify(updatedFights));
                        }
                    });

                $('#newFightModal').modal('hide');
                alert("New fight announced and saved to database as pending!");
                newFightForm.reset();
                document.getElementById('fighter1-img').style.display = 'none';
                document.getElementById('fighter2-img').style.display = 'none';
                fighter1Select.value = "";
                fighter2Select.value = "";
            })
            .catch(error =>
            {
                console.error('Error creating fight:', error);
                alert("Failed to create fight in database! " + error.message);
            });
    });
});