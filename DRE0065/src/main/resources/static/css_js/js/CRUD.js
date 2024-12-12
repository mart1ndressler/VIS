var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
function addCoach(){
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const specialization = document.getElementById('specialization').value;

    const coachData = {
        firstName: firstName,
        lastName: lastName,
        specialization: specialization
    };

    fetch('/api/coaches/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify([coachData])
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Coach added successfully!');
                fetchCoaches();

                document.getElementById('firstName').value = '';
                document.getElementById('lastName').value = '';
                document.getElementById('specialization').value = '';
                $('#addCoachModal').modal('hide');
            }
            else alert('Error adding coach');
        })
        .catch(error =>
        {
            console.error('Error:', error);
            alert('An unexpected error occurred.');
        });
}

function editCoach(id){
    fetch(`/api/coaches/all`)
        .then(response => response.json())
        .then(coaches =>
        {
            const coach = coaches.find(c => c.coachId === id);
            if(coach)
            {
                document.getElementById('editCoachId').value = coach.coachId;
                document.getElementById('editFirstName').value = coach.firstName;
                document.getElementById('editLastName').value = coach.lastName;
                document.getElementById('editSpecialization').value = coach.specialization;
                $('#editCoachModal').modal('show');
            }
        })
        .catch(error => console.error('Error fetching coach:', error));
}

function saveCoachEdit(){
    const id = document.getElementById('editCoachId').value;
    const firstName = document.getElementById('editFirstName').value;
    const lastName = document.getElementById('editLastName').value;
    const specialization = document.getElementById('editSpecialization').value;

    const updatedCoach = {
        firstName: firstName,
        lastName: lastName,
        specialization: specialization
    };

    fetch(`/api/coaches/update/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(updatedCoach)
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Coach updated successfully!');
                $('#editCoachModal').modal('hide');
                fetchCoaches();
            }
            else alert('Error updating coach');
        })
        .catch(error =>
        {
            console.error('Error:', error);
            alert('An unexpected error occurred.');
        });
}

function deleteCoachHandler(id){
    const withDependencies = confirm('Do you want to delete this coach along with all related records?');

    fetch(`/api/coaches/delete/${id}?withDependencies=${withDependencies}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert(withDependencies ? 'Coach and related records deleted successfully!' : 'Coach deleted successfully!');
                fetchCoaches();
            }
            else response.text().then(message => alert(message));
        })
        .catch(error =>
        {
            console.error('Error:', error);
            alert('An unexpected error occurred.');
        });
}

function addCategory(){
    const name = document.getElementById('categoryName').value;
    const minWeight = document.getElementById('minWeight').value;
    const maxWeight = document.getElementById('maxWeight').value;

    const categoryData = {
        name: name,
        minWeight: minWeight,
        maxWeight: maxWeight
    };

    fetch('/api/weight_categories/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify([categoryData])
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Category added successfully!');
                fetchCategories();
                document.getElementById('categoryName').value = '';
                document.getElementById('minWeight').value = '';
                document.getElementById('maxWeight').value = '';
                $('#addCategoryModal').modal('hide');
            }
            else alert('Error adding category');
        })
        .catch(error => {console.error('Error:', error);});
}

function editCategory(id){
    fetch(`/api/weight_categories/all`)
        .then(response => response.json())
        .then(categories =>
        {
            const category = categories.find(c => c.weightCategoryId === id);
            if(category)
            {
                document.getElementById('editCategoryId').value = category.weightCategoryId;
                document.getElementById('editCategoryName').value = category.name;
                document.getElementById('editMinWeight').value = category.minWeight;
                document.getElementById('editMaxWeight').value = category.maxWeight;
                $('#editCategoryModal').modal('show');
            }
        })
        .catch(error => console.error('Error fetching category:', error));
}

function saveCategoryEdit(){
    const id = document.getElementById('editCategoryId').value;
    const name = document.getElementById('editCategoryName').value;
    const minWeight = document.getElementById('editMinWeight').value;
    const maxWeight = document.getElementById('editMaxWeight').value;

    const updatedCategory = {
        name: name,
        minWeight: minWeight,
        maxWeight: maxWeight
    };

    fetch(`/api/weight_categories/update/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(updatedCategory)
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Category updated successfully!');
                fetchCategories();
                $('#editCategoryModal').modal('hide');
            }
            else alert('Error updating category');
        })
        .catch(error => {console.error('Error:', error);});
}

function deleteCategory(id){
    if(!confirm('Are you sure you want to delete this category?')) return;

    fetch(`/api/weight_categories/delete/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Category deleted successfully!');
                fetchCategories();
            }
            else alert('Error deleting category');
        })
        .catch(error => {console.error('Error:', error);});
}

function addEvent(){
    const eventName = document.getElementById('eventName').value;
    const mmaOrganization = document.getElementById('mmaOrganization').value;
    const startDate = new Date(document.getElementById('startOfEvent').value);
    const endDate = new Date(document.getElementById('endOfEvent').value);
    const startOfEvent = `${startDate.getFullYear()}-${(startDate.getMonth() + 1).toString().padStart(2, '0')}-${startDate.getDate().toString().padStart(2, '0')} ${startDate.getHours().toString().padStart(2, '0')}:${startDate.getMinutes().toString().padStart(2, '0')}:${startDate.getSeconds().toString().padStart(2, '0')}`;
    const endOfEvent = `${endDate.getFullYear()}-${(endDate.getMonth() + 1).toString().padStart(2, '0')}-${endDate.getDate().toString().padStart(2, '0')} ${endDate.getHours().toString().padStart(2, '0')}:${endDate.getMinutes().toString().padStart(2, '0')}:${endDate.getSeconds().toString().padStart(2, '0')}`;
    const location = document.getElementById('location').value;

    const eventData = {
        eventName: eventName,
        mmaOrganization: mmaOrganization,
        startOfEvent: startOfEvent,
        endOfEvent: endOfEvent,
        location: location
    };

    fetch('/api/events/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify([eventData])
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Event added successfully!');
                fetchEvents();
                $('#addEventModal').modal('hide');
                document.getElementById('eventName').value = '';
                document.getElementById('mmaOrganization').value = '';
                document.getElementById('startOfEvent').value = '';
                document.getElementById('endOfEvent').value = '';
                document.getElementById('location').value = '';
            }
            else alert('Error adding event');
        })
        .catch(error => console.error('Error:', error));
}

function editEvent(id){
    fetch(`/api/events/all`)
        .then(response => response.json())
        .then(events =>
        {
            const event = events.find(e => e.eventId === id);
            if(event)
            {
                document.getElementById('editEventId').value = event.eventId;
                document.getElementById('editEventName').value = event.eventName;
                document.getElementById('editmmaOrganization').value = event.mmaOrganization;
                document.getElementById('editStartOfEvent').value = event.startOfEvent.substring(0, 16);
                document.getElementById('editEndOfEvent').value = event.endOfEvent.substring(0, 16);
                document.getElementById('editLocation').value = event.location;
                $('#editEventModal').modal('show');
            }
        })
        .catch(error => console.error('Error fetching event:', error));
}

function saveEventEdit(){
    const id = document.getElementById('editEventId').value;
    const eventName = document.getElementById('editEventName').value;
    const mmaOrganization = document.getElementById('editmmaOrganization').value;
    const startOfEvent = formatDateTime(document.getElementById('editStartOfEvent').value);
    const endOfEvent = formatDateTime(document.getElementById('editEndOfEvent').value);
    const location = document.getElementById('editLocation').value;

    const updatedEvent = {
        eventName: eventName,
        mmaOrganization: mmaOrganization,
        startOfEvent: startOfEvent,
        endOfEvent: endOfEvent,
        location: location
    };

    fetch(`/api/events/update/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(updatedEvent)
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Event updated successfully!');
                fetchEvents();
                $('#editEventModal').modal('hide');
            }
            else alert('Error updating event');
        })
        .catch(error => {console.error('Error:', error);});
}

function formatDateTime(dateTime){
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:00`;
}

function deleteEvent(id){
    if(!confirm('Are you sure you want to delete this event?')) return;

    fetch(`/api/events/delete/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Event deleted successfully!');
                fetchEvents();
            }
            else alert('Error deleting event');
        })
        .catch(error => {console.error('Error:', error);});
}

function deleteMMAFighter(fighterId){
    if(confirm("Are you sure you want to delete this fighter?"))
    {
        fetch(`/api/mma-fighters/delete/${fighterId}`, {
            method: 'DELETE'
        })
            .then(response =>
            {
                if(response.ok)
                {
                    alert('Fighter deleted successfully!');
                    fetchMMAFighters();
                }
                else response.json().then(data => alert('Error deleting fighter: ' + data.message));
            })
            .catch(error => console.error('Error:', error));
    }
}

function editMMAFighter(fighterId){
    fetch(`/api/mma-fighters/${fighterId}`)
        .then(response => response.json())
        .then(fighter =>
        {
            document.getElementById('editFighterId').value = fighter.fighterId;
            document.getElementById('editFighterFirstName').value = fighter.first_name;
            document.getElementById('editFighterLastName').value = fighter.last_name;
            document.getElementById('editFighterWeight').value = fighter.weight;
            document.getElementById('editFighterHeight').value = fighter.height;
            document.getElementById('editFighterReach').value = fighter.reach;
            document.getElementById('editFighterNationality').value = fighter.nationality;
            document.getElementById('editFighterRanking').value = fighter.ranking;
            document.getElementById('editFighterFights').value = fighter.fights;
            document.getElementById('editFighterPoints').value = fighter.points;

            fetch('/api/weight_categories/all')
                .then(response => response.json())
                .then(categories =>
                {
                    const selectElement = document.getElementById('editFighterWeightCategory');
                    selectElement.innerHTML = '<option value="">Select Weight Category</option>';

                    categories.forEach(category =>
                    {
                        const option = document.createElement('option');
                        option.value = category.weightCategoryId;
                        option.textContent = category.name;

                        if(fighter.weight_category_id && fighter.weight_category_id.weightCategoryId === category.weightCategoryId) option.selected = true;
                        selectElement.appendChild(option);
                    });
                })
                .catch(error => console.error('Error fetching weight categories:', error));
            $('#editMMAFighterModal').modal('show');
        })
        .catch(error => console.error('Error fetching fighter:', error));
}

function saveMMAFighterEdit(){
    const fighterData = {
        fighterId: parseInt(document.getElementById('editFighterId').value),
        first_name: document.getElementById('editFighterFirstName').value,
        last_name: document.getElementById('editFighterLastName').value,
        weight: document.getElementById('editFighterWeight').value,
        height: document.getElementById('editFighterHeight').value,
        reach: document.getElementById('editFighterReach').value,
        nationality: document.getElementById('editFighterNationality').value,
        ranking: document.getElementById('editFighterRanking').value,
        fights: parseInt(document.getElementById('editFighterFights').value),
        points: parseInt(document.getElementById('editFighterPoints').value),
        weight_category_id: {
            weightCategoryId: parseInt(document.getElementById('editFighterWeightCategory').value)
        }
    };

    fetch(`/api/mma-fighters/update/${fighterData.fighterId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(fighterData)
    })
        .then(response =>
        {
            if(!response.ok) return response.json().then(data => {throw new Error(`Error updating fighter: ${data.message}`);});
            alert('Fighter updated successfully!');
            fetchMMAFighters();
            $('#editMMAFighterModal').modal('hide');
        })
        .catch(error => console.error('Error updating fighter:', error));
}

function populateWeightCategoriesForAdd(){
    fetch('/api/weight_categories/all')
        .then(response => response.json())
        .then(categories =>
        {
            const selectElement = document.getElementById('fighterWeightCategory');
            selectElement.innerHTML = '<option value="">Select Weight Category</option>';

            categories.forEach(category =>
            {
                const option = document.createElement('option');
                option.value = category.weightCategoryId;
                option.textContent = category.name;
                selectElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching weight categories:', error));
}

function addMMAFighter(){
    const weightCategoryId = parseInt(document.getElementById('fighterWeightCategory').value);

    if(!weightCategoryId || isNaN(weightCategoryId))
    {
        alert('Please select a valid weight category!');
        return;
    }

    const fighterData = {
        first_name: document.getElementById('fighterFirstName').value,
        last_name: document.getElementById('fighterLastName').value,
        weight: document.getElementById('fighterWeight').value,
        height: document.getElementById('fighterHeight').value,
        reach: document.getElementById('fighterReach').value,
        nationality: document.getElementById('fighterNationality').value,
        ranking: document.getElementById('fighterRanking').value,
        fights: parseInt(document.getElementById('fighterFights').value),
        points: parseInt(document.getElementById('fighterPoints').value),
        weight_category: {
            name: document.getElementById('fighterWeightCategory').options[document.getElementById('fighterWeightCategory').selectedIndex].text
        }
    };

    fetch('/api/mma-fighters/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify([fighterData])
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('Fighter added successfully!');
                document.getElementById('fighterFirstName').value = '';
                document.getElementById('fighterLastName').value = '';
                document.getElementById('fighterWeight').value = '';
                document.getElementById('fighterHeight').value = '';
                document.getElementById('fighterReach').value = '';
                document.getElementById('fighterNationality').value = '';
                document.getElementById('fighterRanking').value = '';
                document.getElementById('fighterFights').value = '';
                document.getElementById('fighterPoints').value = '';
                document.getElementById('fighterWeightCategory').value = '';
                fetchMMAFighters();
                $('#addMMAFighterModal').modal('hide');
            }
            else response.json().then(data => alert('Error adding fighter: ' + data.message));
        })
        .catch(error => console.error('Error:', error));
}

populateWeightCategoriesForAdd();

function deletePreparation(id){
    if(confirm("Are you sure you want to delete this preparation?"))
    {
        fetch(`/api/preparations/delete/${id}`, {
            method: 'DELETE',
        })
            .then(response =>
            {
                if(response.ok)
                {
                    alert("Preparation deleted successfully!");
                    fetchPreparations();
                }
                else response.json().then(data => alert("Error deleting preparation: " + data.message));
            })
            .catch(error => console.error('Error:', error));
    }
}

function editPreparation(preparationId){
    document.getElementById('editPreparationModal').dataset.preparationId = preparationId;

    fetch(`/api/preparations/${preparationId}`)
        .then(response => response.json())
        .then(preparation =>
        {
            console.log(preparation);
            if(preparation)
            {
                document.getElementById('editstartOfPreparation').value = preparation.start_of_preparation || '';
                document.getElementById('editendOfPreparation').value = preparation.end_of_preparation || '';
                document.getElementById('editmmaClub').value = preparation.mma_club || '';
                document.getElementById('editclubRegion').value = preparation.club_region || '';
                document.getElementById('editmmaFighter').value = preparation['mma-fighter'] ? preparation['mma-fighter'].fighterId : '';
                document.getElementById('editcoach').value = preparation.coach ? preparation.coach.coachId : '';
            }
            else console.error("No dates of preparation were found.");
            console.log(preparation.coach);
            $('#editPreparationModal').modal('show');
        })
        .catch(error => console.error("Error loading preparation:", error));
}

function savePreparationChanges(){
    const preparationId = parseInt(document.getElementById('editPreparationModal').dataset.preparationId);
    const startOfPreparation = formatDateTime(document.getElementById('editstartOfPreparation').value);
    const endOfPreparation = formatDateTime(document.getElementById('editendOfPreparation').value);
    const mmaClub = document.getElementById('editmmaClub').value;
    const clubRegion = document.getElementById('editclubRegion').value;
    const mmaFighterId = parseInt(document.getElementById('editmmaFighter').value);
    const coachId = parseInt(document.getElementById('editcoach').value);

    const preparationData = {
        start_of_preparation: startOfPreparation,
        end_of_preparation: endOfPreparation,
        mma_club: mmaClub,
        club_region: clubRegion,
        'mma-fighter': {fighterId: mmaFighterId},
        coach: {coachId: coachId}
    };

    fetch(`/api/preparations/update/${preparationId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(preparationData)
    })
        .then(response =>
        {
            if(response.ok)
            {
                alert('The preparation has been successfully modified!');
                fetchPreparations();
                $('#editPreparationModal').modal('hide');
            }
            else response.text().then(text => {alert('There was an error in the preparation adjustment: ' + text);});
        })
        .catch(error => console.error('Error:', error));
}

function populateFightersForEdit(selectedFighterId){
    fetch('/api/mma-fighters/all')
        .then(response => response.json())
        .then(fighters =>
        {
            const selectElement = document.getElementById('editmmaFighter');
            selectElement.innerHTML = '<option value="">Select Fighter...</option>';

            fighters.forEach(fighter =>
            {
                const option = document.createElement('option');
                option.value = fighter.fighterId;
                option.textContent = `${fighter.first_name} ${fighter.last_name}`;
                if(fighter.fighterId === selectedFighterId) option.selected = true;
                selectElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching fighters:', error));
}

function populateCoachesForEdit(selectedCoachId){
    fetch('/api/coaches/all')
        .then(response => response.json())
        .then(coaches =>
        {
            const selectElement = document.getElementById('editcoach');
            selectElement.innerHTML = '<option value="">Select Coach...</option>';

            coaches.forEach(coach =>
            {
                const option = document.createElement('option');
                option.value = coach.coachId;
                option.textContent = `${coach.firstName} ${coach.lastName}`;
                if(coach.coachId === selectedCoachId) option.selected = true;
                selectElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching coaches:', error));
}

populateCoachesForEdit();
populateFightersForEdit();

function addPreparation(){
    const startOfPreparation = formatDateTime(document.getElementById('startOfPreparation').value);
    const endOfPreparation = formatDateTime(document.getElementById('endOfPreparation').value);
    const mmaClub = document.getElementById('mmaClub').value.trim();
    const clubRegion = document.getElementById('clubRegion').value.trim();
    const mmaFighterId = parseInt(document.getElementById('mmaFighter').value);
    const coachId = parseInt(document.getElementById('coach').value);

    if(!startOfPreparation || !endOfPreparation || !mmaClub || !clubRegion || isNaN(mmaFighterId) || isNaN(coachId))
    {
        alert('Please fill in all fields correctly.');
        return;
    }

    const preparationData = {
        start_of_preparation: startOfPreparation,
        end_of_preparation: endOfPreparation,
        mma_club: mmaClub,
        club_region: clubRegion,
        'mma-fighter': {fighterId: mmaFighterId},
        coach: {coachId: coachId}
    };

    fetch('/api/preparations/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(preparationData)
    })
        .then(response =>
        {
            if(response.ok) return response.text();
            else return response.text().then(text => {throw new Error(text)});
        })
        .then(message =>
        {
            alert(message);
            $('#addPreparationModal').modal('hide');
            document.getElementById('addPreparationForm').reset();
            fetchPreparations();
        })
        .catch(error =>
        {
            console.error('Error:', error);
            alert('There was an error when adding the preparation: ' + error.message);
        });
}

function populateFighters_prep(){
    fetch('/api/mma-fighters/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch fighters');
            return response.json();
        })
        .then(fighters =>
        {
            const selectElement = document.getElementById('mmaFighter');
            selectElement.innerHTML = '<option value="">Select Fighter</option>';

            fighters.forEach(fighter =>
            {
                const option = document.createElement('option');
                option.value = fighter.fighterId;
                option.textContent = `${fighter.first_name} ${fighter.last_name}`;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching fighters:', error);
            alert('There was an error when loading fighters.');
        });
}

function populateCoaches(){
    fetch('/api/coaches/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch coaches');
            return response.json();
        })
        .then(coaches =>
        {
            const selectElement = document.getElementById('coach');
            selectElement.innerHTML = '<option value="">Select Coach</option>';

            coaches.forEach(coach =>
            {
                const option = document.createElement('option');
                option.value = coach.coachId;
                option.textContent = `${coach.firstName} ${coach.lastName}`;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching coaches:', error);
            alert('There was an error loading the coaches.');
        });
}

$('#addPreparationModal').on('show.bs.modal', function (){
    populateFighters_prep();
    populateCoaches();
});

function populateWeightCategories(selectId){
    fetch('/api/weight_categories/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch weight categories');
            return response.json();
        })
        .then(categories =>
        {
            const selectElement = document.getElementById(selectId);
            selectElement.innerHTML = '<option value="">Select Weight Category</option>';

            categories.forEach(cat =>
            {
                const option = document.createElement('option');
                option.value = cat.weightCategoryId;
                option.textContent = cat.name;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching weight categories:', error);
            alert('There was an error when loading weight categories.');
        });
}

function populateEvents(selectId){
    fetch('/api/events/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch events');
            return response.json();
        })
        .then(events =>
        {
            const selectElement = document.getElementById(selectId);
            selectElement.innerHTML = '<option value="">Select Event</option>';

            events.forEach(evt =>
            {
                const option = document.createElement('option');
                option.value = evt.eventId;
                option.textContent = `${evt.eventName} (${evt.location})`;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching events:', error);
            alert('There was an error when loading events.');
        });
}

$('#addFightModal').on('show.bs.modal', function (){
    populateWeightCategories('fightWeightCategory');
    populateEvents('fightEvent');
});

$('#editFightModal').on('show.bs.modal', function (){
    populateWeightCategories('editFightWeightCategory');
    populateEvents('editFightEvent');
});

function addFight(){
    const date = formatDateTime(document.getElementById('fightDate').value);
    const result = document.getElementById('fightResult').value.trim();
    const typeOfResult = document.getElementById('fightTypeOfResult').value.trim();
    const weightCategoryId = parseInt(document.getElementById('fightWeightCategory').value);
    const eventId = parseInt(document.getElementById('fightEvent').value);

    if(!date || !result || !typeOfResult || isNaN(weightCategoryId) || isNaN(eventId))
    {
        alert('Please fill in all fields correctly.');
        return;
    }

    const fightData = {
        date: date,
        result: result,
        type_of_result: typeOfResult,
        weight_category: {weightCategoryId: weightCategoryId},
        event: {eventId: eventId}
    };

    const payload = [fightData];
    fetch('/api/fights/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            return response.text();
        })
        .then(message =>
        {
            alert(message);
            $('#addFightModal').modal('hide');
            document.getElementById('addFightForm').reset();
            fetchFights();
        })
        .catch(error =>
        {
            console.error('Error adding fight:', error);
            alert('There was an error when adding a fighter: ' + error.message);
        });
}

function editFight(fightId){
    fetch(`/api/fights/${fightId}`)
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch fight details');
            return response.json();
        })
        .then(fight =>
        {
            const dateObj = new Date(fight.date.replace(' ', 'T'));
            const year = dateObj.getFullYear();
            const month = ('0' + (dateObj.getMonth()+1)).slice(-2);
            const day = ('0' + dateObj.getDate()).slice(-2);
            const hours = ('0' + dateObj.getHours()).slice(-2);
            const minutes = ('0' + dateObj.getMinutes()).slice(-2);
            const dateLocal = `${year}-${month}-${day}T${hours}:${minutes}`;

            document.getElementById('editFightDate').value = dateLocal;
            document.getElementById('editFightResult').value = fight.result || '';
            document.getElementById('editFightTypeOfResult').value = fight.type_of_result || '';

            setTimeout(() =>
            {
                document.getElementById('editFightWeightCategory').value = fight.weight_category ? fight.weight_category.weightCategoryId : '';
                document.getElementById('editFightEvent').value = fight.event ? fight.event.eventId : '';
            }, 500);

            document.getElementById('editFightModal').dataset.fightId = fightId;
            $('#editFightModal').modal('show');
        })
        .catch(error =>
        {
            console.error('Error loading fight details:', error);
            alert('There was an error loading the fighter details.');
        });
}

function saveFightChanges(){
    const fightId = parseInt(document.getElementById('editFightModal').dataset.fightId);
    const date = formatDateTime(document.getElementById('editFightDate').value);
    const result = document.getElementById('editFightResult').value.trim();
    const typeOfResult = document.getElementById('editFightTypeOfResult').value.trim();
    const weightCategoryId = parseInt(document.getElementById('editFightWeightCategory').value);
    const eventId = parseInt(document.getElementById('editFightEvent').value);

    if(!date || !result || !typeOfResult || isNaN(weightCategoryId) || isNaN(eventId))
    {
        alert('Please fill in all fields correctly.');
        return;
    }

    const fightData = {
        date: date,
        result: result,
        type_of_result: typeOfResult,
        weight_category: {weightCategoryId: weightCategoryId},
        event: {eventId: eventId}
    };

    fetch(`/api/fights/update/${fightId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(fightData)
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            return response.json();
        })
        .then(updatedFight =>
        {
            alert('Fight has been successfully edited!');
            $('#editFightModal').modal('hide');
            fetchFights();
        })
        .catch(error =>
        {
            console.error('Error saving fight changes:', error);
            alert('There was an error in editing the fights: ' + error.message);
        });
}

function deleteFight(fightId){
    if(!confirm('Are you sure you want to delete this fight?')) return;

    fetch(`/api/fights/delete/${fightId}`, {
        method: 'DELETE'
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            alert('Fight deleted!');
            fetchFights();
        })
        .catch(error =>
        {
            console.error('Error deleting fight:', error);
            alert('There was an error while deleting a fighter: ' + error.message);
        });
}

function populateFights(selectId, selectedFightId = null){
    return fetch('/api/fights/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch fights');
            return response.json();
        })
        .then(fights =>
        {
            const selectElement = document.getElementById(selectId);
            selectElement.innerHTML = '<option value="">Select Fight</option>';

            fights.forEach(fight =>
            {
                const option = document.createElement('option');
                option.value = fight.fightId;
                option.textContent = `Fight ID: ${fight.fightId}, Event: ${fight.event.eventName}`;
                if(fight.fightId === selectedFightId) option.selected = true;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching fights:', error);
            alert('There was an error when loading Fights.');
        });
}

function populateFighters(selectId, selectedFighterId = null){
    return fetch('/api/mma-fighters/all')
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch fighters');
            return response.json();
        })
        .then(fighters =>
        {
            const selectElement = document.getElementById(selectId);
            selectElement.innerHTML = '<option value="">Select Fighter</option>';

            fighters.forEach(fighter =>
            {
                const option = document.createElement('option');
                option.value = fighter.fighterId;
                option.textContent = `${fighter.first_name} ${fighter.last_name}`;
                if(fighter.fighterId === selectedFighterId) option.selected = true;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching fighters:', error);
            alert('There was an error loading Fighters.');
        });
}

function addMMAFight(){
    const fightId = parseInt(document.getElementById('addFightSelect').value);
    const fighterId = parseInt(document.getElementById('addFighterSelect').value);

    if(isNaN(fightId) || isNaN(fighterId))
    {
        alert('Please select both Fight and Fighter.');
        return;
    }

    const mmaFightData = {
        fight: {fightId: fightId},
        fighter: {fighterId: fighterId}
    };

    const payload = [mmaFightData];
    fetch('/api/mmafights/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            return response.text();
        })
        .then(message =>
        {
            alert(message);
            $('#addMMAFightModal').modal('hide');
            document.getElementById('addMMAFightForm').reset();
            fetchMMAFights();
        })
        .catch(error =>
        {
            console.error('Error adding MMAFight:', error);
            alert('There was an error when adding MMAFight: ' + error.message);
        });
}

function editMMAFight(mmaFightId){
    fetch(`/api/mmafights/${mmaFightId}`)
        .then(response =>
        {
            if(!response.ok) throw new Error('Failed to fetch MMAFight details');
            return response.json();
        })
        .then(mmaFight =>
        {
            const fightId = mmaFight.fight.fightId;
            const fighterId = mmaFight.fighter.fighterId;

            Promise.all([
                populateFights('editFightSelect', fightId),
                populateFighters('editFighterSelect', fighterId)
            ])
                .then(() =>
                {
                    document.getElementById('editMMAFightModal').dataset.mmaFightId = mmaFightId;
                    $('#editMMAFightModal').modal('show');
                })
                .catch(error =>
                {
                    console.error('Error populating selects for editing MMAFight:', error);
                    alert('An error occurred when loading the dropdown menus for editing MMAFight.');
                });
        })
        .catch(error =>
        {
            console.error('Error loading MMAFight details:', error);
            alert('An error occurred while loading MMAFight details.');
        });
}

function saveMMAFightChanges(){
    const mmaFightId = parseInt(document.getElementById('editMMAFightModal').dataset.mmaFightId);
    const fightId = parseInt(document.getElementById('editFightSelect').value);
    const fighterId = parseInt(document.getElementById('editFighterSelect').value);

    if(isNaN(fightId) || isNaN(fighterId))
    {
        alert('Please select both Fight and Fighter.');
        return;
    }

    const mmaFightData = {
        fight: {fightId: fightId},
        fighter: {fighterId: fighterId}
    };

    fetch(`/api/mmafights/update/${mmaFightId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(mmaFightData)
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            return response.json();
        })
        .then(updatedMMAFight =>
        {
            alert('MMAFight has been successfully modified!');
            $('#editMMAFightModal').modal('hide');
            fetchMMAFights();
        })
        .catch(error =>
        {
            console.error('Error saving MMAFight changes:', error);
            alert('There was an error while editing MMAFight: ' + error.message);
        });
}

function deleteMMAFight(mmaFightId){
    if(!confirm('Are you sure you want to delete this MMAFight?')) return;

    fetch(`/api/mmafights/delete/${mmaFightId}`, {
        method: 'DELETE'
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => {throw new Error(text)});
            alert('MMAFight deleted!');
            fetchMMAFights();
        })
        .catch(error =>
        {
            console.error('Error deleting MMAFight:', error);
            alert('There was an error when deleting MMAFight: ' + error.message);
        });
}

$('#addMMAFightModal').on('show.bs.modal', function (){
    populateFights('addFightSelect')
        .then(() => populateFighters('addFighterSelect'))
        .catch(error => console.error('Error populating selects:', error));
});

$('#editMMAFightModal').on('show.bs.modal', function (){
    populateFights('editFightSelect')
        .then(() => populateFighters('editFighterSelect'))
        .catch(error => console.error('Error populating selects:', error));
});

function addStat(){
    const wins = document.getElementById('addWins').value;
    const losses = document.getElementById('addLosses').value;
    const draws = document.getElementById('addDraws').value;
    const kos = document.getElementById('addKos').value;
    const tkos = document.getElementById('addTkos').value;
    const submissions = document.getElementById('addSubmissions').value;
    const decisions = document.getElementById('addDecisions').value;
    const fighterId = document.getElementById('addFighterStatSelect').value;

    if(!fighterId)
    {
        alert('Please select a fighter.');
        return;
    }

    const stat = {
        wins: parseInt(wins),
        losses: parseInt(losses),
        draws: parseInt(draws),
        kos: parseInt(kos),
        tkos: parseInt(tkos),
        submissions: parseInt(submissions),
        decisions: parseInt(decisions),
        "mma-fighter": {fighterId: parseInt(fighterId)}
    };

    fetch('/api/stats/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(stat)
    })
        .then(response =>
        {
            if(response.ok)
            {
                $('#addStatModal').modal('hide');
                fetchStats();
                alert('Stat added successfully!');
                document.getElementById('addStatForm').reset();
            }
            else response.text().then(text => alert('Error adding stat: ' + text));
        })
        .catch(error =>
        {
            console.error('Error adding stat:', error);
            alert('Error adding stat.');
        });
}

function editStat(id){
    fetch(`/api/stats/all`)
        .then(response => response.json())
        .then(data =>
        {
            const stat = data.find(s => s.statsId === id);
            if(stat)
            {
                document.getElementById('editStatsId').value = stat.statsId;
                document.getElementById('editWins').value = stat.wins;
                document.getElementById('editLosses').value = stat.losses;
                document.getElementById('editDraws').value = stat.draws;
                document.getElementById('editKos').value = stat.kos;
                document.getElementById('editTkos').value = stat.tkos;
                document.getElementById('editSubmissions').value = stat.submissions;
                document.getElementById('editDecisions').value = stat.decisions;
                document.getElementById('editFighterStatSelect').value = stat['mma-fighter']?.fighterId || '';
                $('#editStatModal').modal('show');
            }
            else alert('Stat not found.');
        })
        .catch(error =>
        {
            console.error('Error fetching stats for edit:', error);
            alert('Error fetching stat data.');
        });
}

function saveStatChanges(){
    const statsId = document.getElementById('editStatsId').value;
    const wins = document.getElementById('editWins').value;
    const losses = document.getElementById('editLosses').value;
    const draws = document.getElementById('editDraws').value;
    const kos = document.getElementById('editKos').value;
    const tkos = document.getElementById('editTkos').value;
    const submissions = document.getElementById('editSubmissions').value;
    const decisions = document.getElementById('editDecisions').value;
    const fighterId = document.getElementById('editFighterStatSelect').value;

    if(!fighterId)
    {
        alert('Please select a fighter.');
        return;
    }

    const stat = {
        statsId: parseInt(statsId),
        wins: parseInt(wins),
        losses: parseInt(losses),
        draws: parseInt(draws),
        kos: parseInt(kos),
        tkos: parseInt(tkos),
        submissions: parseInt(submissions),
        decisions: parseInt(decisions),
        "mma-fighter": {fighterId: parseInt(fighterId)}
    };

    fetch(`/api/stats/update/${statsId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(stat)
    })
        .then(response =>
        {
            if(response.ok)
            {
                $('#editStatModal').modal('hide');
                fetchStats();
                alert('Stat updated successfully!');
            }
            else response.text().then(text => alert('Error updating stat: ' + text));
        })
        .catch(error =>
        {
            console.error('Error updating stat:', error);
            alert('Error updating stat.');
        });
}

function deleteStat(id){
    if(!confirm('Are you sure you want to delete this Stat?')) return;

    fetch(`/api/stats/delete/${id}`, {
        method: 'DELETE'
    })
        .then(response =>
        {
            if(!response.ok) return response.text().then(text => { throw new Error(text) });
            alert('Stat deleted successfully!');
            fetchStats();
        })
        .catch(error =>
        {
            console.error('Error deleting stat:', error);
            alert('Error deleting stat: ' + error.message);
        });
}

function populateFightersstats(selectId){
    fetch('/api/mma-fighters/all')
        .then(response => response.json())
        .then(data =>
        {
            const selectElement = document.getElementById(selectId);
            selectElement.innerHTML = '<option value="">Select Fighter</option>';
            data.forEach(fighter =>
            {
                const option = document.createElement('option');
                option.value = fighter.fighterId;
                option.textContent = `${fighter.first_name} ${fighter.last_name}`;
                selectElement.appendChild(option);
            });
        })
        .catch(error =>
        {
            console.error('Error fetching fighters:', error);
            alert('Error loading fighters.');
        });
}

$(document).ready(function(){
    populateFightersstats('addFighterStatSelect');
    populateFightersstats('editFighterStatSelect');
});