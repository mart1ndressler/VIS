document.addEventListener('DOMContentLoaded', function (){
    const fighterPhoto = document.getElementById('fighter-photo');
    const fightsTableBody = document.querySelector('#fights-table tbody');
    const pendingFightsTableBody = document.querySelector('#pending-fights-table tbody');
    const userNameElement = document.getElementById('user-name');
    const fullName = userNameElement.textContent.trim();
    const [userFirstName, userLastName] = fullName.split(' ');

    let fightersData = [];

    function loadFighterPhoto(id){
        fighterPhoto.src = `/css_js/images/${id}.png`;
        fighterPhoto.onerror = function () {this.src = '/css_js/images/logo.png';};
    }

    function populateFightsTable(fights, loggedInFighterId){
        fightsTableBody.innerHTML = '';
        fights.forEach(item =>
        {
            const fight = item.fight;
            const loggedInFighterInFight = item.fighter.fighterId === loggedInFighterId;

            if(loggedInFighterInFight)
            {
                const opponent = fights.find(opp => opp.fight.fightId === fight.fightId && opp.fighter.fighterId !== loggedInFighterId);

                if(opponent)
                {
                    const opponentData = opponent.fighter;
                    const row = document.createElement('tr');
                    const opponentCell = document.createElement('td');
                    const opponentImg = document.createElement('img');

                    opponentImg.src = `/css_js/images/${opponentData.fighterId}.png`;
                    opponentImg.alt = `${opponentData.first_name} ${opponentData.last_name}`;
                    opponentImg.width = 40;
                    opponentImg.height = 40;
                    opponentImg.classList.add('mr-2');
                    opponentImg.onerror = function () { this.src = '/css_js/images/logo.png'; };
                    opponentCell.appendChild(opponentImg);
                    opponentCell.insertAdjacentHTML('beforeend', `${opponentData.first_name} ${opponentData.last_name}`);
                    row.appendChild(opponentCell);

                    const dateCell = document.createElement('td');
                    const fightDate = new Date(fight.date);
                    dateCell.textContent = fightDate.toLocaleDateString() + ' ' + fightDate.toLocaleTimeString();
                    row.appendChild(dateCell);

                    const eventCell = document.createElement('td');
                    eventCell.textContent = fight.event.eventName;
                    row.appendChild(eventCell);

                    const typeResultCell = document.createElement('td');
                    typeResultCell.textContent = fight.type_of_result;
                    row.appendChild(typeResultCell);
                    fightsTableBody.appendChild(row);
                }
            }
        });
    }

    function renderPendingFightsRow(opponentData, fightDate, eventName, fightId, hasAccepted){
        const row = document.createElement('tr');
        const opponentCell = document.createElement('td');
        const opponentImg = document.createElement('img');

        opponentImg.src = `/css_js/images/${opponentData.fighterId}.png`;
        opponentImg.alt = `${opponentData.first_name} ${opponentData.last_name}`;
        opponentImg.width = 40;
        opponentImg.height = 40;
        opponentImg.classList.add('mr-2');
        opponentImg.onerror = function () {this.src = '/css_js/images/logo.png';};
        opponentCell.appendChild(opponentImg);
        opponentCell.insertAdjacentHTML('beforeend', `${opponentData.first_name} ${opponentData.last_name}`);
        row.appendChild(opponentCell);

        const dateCell = document.createElement('td');
        dateCell.textContent = fightDate.toLocaleDateString() + ' ' + fightDate.toLocaleTimeString();
        row.appendChild(dateCell);

        const eventCell = document.createElement('td');
        eventCell.textContent = eventName;
        row.appendChild(eventCell);

        const actionsCell = document.createElement('td');

        if(hasAccepted)
        {
            const message = document.createElement('span');
            message.textContent = 'Fight accepted, waiting for opponent';
            actionsCell.appendChild(message);
        }
        else
        {
            const acceptButton = document.createElement('span');
            acceptButton.style.cursor = 'pointer';
            acceptButton.style.marginRight = '10px';
            acceptButton.innerHTML = '<i class="fa fa-check"></i>';
            acceptButton.addEventListener('click', () => handleAcceptAction(fightId));

            const refuseButton = document.createElement('span');
            refuseButton.style.cursor = 'pointer';
            refuseButton.innerHTML = '<i class="fa fa-times"></i>';
            refuseButton.addEventListener('click', () => handleRefuseAction(fightId));

            actionsCell.appendChild(acceptButton);
            actionsCell.appendChild(refuseButton);
        }
        row.appendChild(actionsCell);
        return row;
    }

    function handleAcceptAction(fightId){
        let newPendingFights = JSON.parse(localStorage.getItem('newPendingFights')) || [];
        const loggedInFighter = fightersData.find(f => f.first_name.toLowerCase() === userFirstName.toLowerCase() && f.last_name.toLowerCase() === userLastName.toLowerCase());
        if(!loggedInFighter) return;

        newPendingFights = newPendingFights.map(f =>
        {
            if(f.fightId === fightId) f.fighters = f.fighters.map(fg => {if(fg.fighterId === loggedInFighter.fighterId) fg.accepted = true; return fg;});
            return f;
        });

        localStorage.setItem('newPendingFights', JSON.stringify(newPendingFights));

        const fightData = newPendingFights.find(f => f.fightId === fightId);
        if(fightData && fightData.fighters.every(fg => fg.accepted))
        {
            const mmaFightsToAdd = fightData.fighters.map(fg => ({fight: { fightId: fightData.fightId }, fighter: { fighterId: fg.fighterId }}));

            fetch('/api/mmafights/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
                },
                body: JSON.stringify(mmaFightsToAdd)
            })
                .then(res =>
                {
                    if(!res.ok) throw new Error('Failed to add MMAFights.');
                    return res.text();
                })
                .then(() =>
                {
                    removeFromPendingFights(fightId);
                    alert("Fight accepted by both fighters and moved to My Fights!");
                })
                .catch(err => console.error('Error adding MMAFights:', err));
        }
        else loadPendingFights();
    }

    function handleRefuseAction(fightId){
        fetch(`/api/fights/delete/${fightId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            }
        })
            .then(res =>
            {
                if(!res.ok) throw new Error('Failed to delete fight');
                return res.text();
            })
            .then(() =>
            {
                removeFromPendingFights(fightId);
                alert("Fight has been refused and removed from Pending Fights.");
            })
            .catch(err => console.error('Error deleting fight:', err));
    }

    function removeFromPendingFights(fightId){
        let newPendingFights = JSON.parse(localStorage.getItem('newPendingFights')) || [];
        newPendingFights = newPendingFights.filter(f => f.fightId !== fightId);
        localStorage.setItem('newPendingFights', JSON.stringify(newPendingFights));
        loadPendingFights();
    }

    function populatePendingFightsTable(pendingFights, loggedInFighterId){
        pendingFightsTableBody.innerHTML = '';

        pendingFights.forEach(item =>
        {
            const fight = item.fight;
            const challenger = item.challenger;
            const isParticipant = (challenger && challenger.fighterId === loggedInFighterId) || (fight.opponentId === loggedInFighterId);

            if(fight.status === 'pending' && isParticipant)
            {
                let opponentData;
                if(challenger && challenger.fighterId === loggedInFighterId)  opponentData = {fighterId: fight.opponentId, first_name: "Unknown", last_name: "Fighter"};
                else opponentData = challenger;

                const fightDate = new Date(fight.date);
                const eventName = fight.event.eventName;
                const fightId = fight.fightId;
                const row = renderPendingFightsRow(opponentData, fightDate, eventName, fightId, false);
                pendingFightsTableBody.appendChild(row);
            }
        });

        const newPendingFightsJSON = localStorage.getItem('newPendingFights');
        if(newPendingFightsJSON)
        {
            const newPendingFights = JSON.parse(newPendingFightsJSON);
            const loggedInFighter = fightersData.find(f => f.first_name.toLowerCase() === userFirstName.toLowerCase() && f.last_name.toLowerCase() === userLastName.toLowerCase());
            if(!loggedInFighter) return;

            newPendingFights.forEach(newPendingFight =>
            {
                const isParticipant = newPendingFight.fighters.some(f => f.fighterId === loggedInFighter.fighterId);
                if(isParticipant && newPendingFight.fightId)
                {
                    const opponentData = newPendingFight.fighters.find(f => f.fighterId !== loggedInFighter.fighterId);
                    const eventName = newPendingFight.event.eventName;
                    const fightDate = new Date(newPendingFight.date);
                    const hasAccepted = newPendingFight.fighters.some(f => f.fighterId === loggedInFighter.fighterId && f.accepted);
                    const row = renderPendingFightsRow(opponentData, fightDate, eventName, newPendingFight.fightId, hasAccepted);
                    pendingFightsTableBody.appendChild(row);
                }
            });
        }
    }

    function loadFights(fightsData, allFightersData, loggedInFighterId){
        fightersData = allFightersData;
        populateFightsTable(fightsData, loggedInFighterId);
        populatePendingFightsTable(fightsData, loggedInFighterId);
    }

    function loadPendingFights(){
        fetch('/api/mmafights/all')
            .then(response =>
            {
                if (!response.ok) throw new Error('Network response was not ok ' + response.statusText);
                return response.json();
            })
            .then(fightsData =>
            {
                fetch('/api/mma-fighters/all')
                    .then(response =>
                    {
                        if (!response.ok) throw new Error('Network response was not ok ' + response.statusText);
                        return response.json();
                    })
                    .then(allFightersData =>
                    {
                        const loggedInFighter = allFightersData.find(fighter =>
                            fighter.first_name.toLowerCase() === userFirstName.toLowerCase() &&
                            fighter.last_name.toLowerCase() === userLastName.toLowerCase()
                        );
                        if(loggedInFighter)
                        {
                            loadFighterPhoto(loggedInFighter.fighterId);
                            loadFights(fightsData, allFightersData, loggedInFighter.fighterId);
                        }
                        else fighterPhoto.src = '/css_js/images/logo.png';
                    });
            })
            .catch(error =>
            {
                fightsTableBody.innerHTML = '<tr><td colspan="4">Failed to load fights data.</td></tr>';
                pendingFightsTableBody.innerHTML = '<tr><td colspan="4">Failed to load pending fights.</td></tr>';
            });
    }
    loadPendingFights();
});