function toggleInfo(){
    var infos = document.getElementsByClassName('additional-info');
    for (var i = 0; i < infos.length; i++) infos[i].style.display = infos[i].style.display === 'none' ? 'block' : 'none';
}

showMMAFighters();

function hideFilters(){
    document.getElementById('mma-fighter-filters').style.display = 'none';
    document.getElementById('stats-filters').style.display = 'none';
}

function hideAllTables(){
    document.getElementById('coaches-table').style.display = 'none';
    document.getElementById('events-table').style.display = 'none';
    document.getElementById('weight_categories-table').style.display = 'none';
    document.getElementById('mma_fighters-table').style.display = 'none';
    document.getElementById('stats-table').style.display = 'none';
    document.getElementById('preparations-table').style.display = 'none';
    document.getElementById('fights-table').style.display = 'none';
    document.getElementById('mma-fights-table').style.display = 'none';
}

function hideCRUDBtns(){
    document.getElementById('addCoachBtn').style.display = 'none';
}

function showCoaches(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('coaches-table').style.display = 'table';
    document.getElementById('addCoachBtn').style.display = 'block';
    fetchCoaches();
}

function showEvents(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('events-table').style.display = 'table';
    fetchEvents();
}

function showWeightCategories(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('weight_categories-table').style.display = 'table';
    fetchCategories();
}

function showMMAFighters(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('mma_fighters-table').style.display = 'table';
    document.getElementById('mma-fighter-filters').style.display = 'block';
    fetchMMAFighters();
}

function showStats(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('stats-table').style.display = 'table';
    document.getElementById('stats-filters').style.display = 'block';
    fetchStats();
    fetchWins();
}

function showPreparations(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('preparations-table').style.display = 'table';
    fetchPreparations();
}

function showFights(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('fights-table').style.display = 'table';
    fetchFights();
}

function showMMAFights(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('mma-fights-table').style.display = 'table';
    fetchMMAFights();
}

function fetchCoaches(){
    fetch('/api/coaches/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('coaches-table-body');
            tableBody.innerHTML = '';
            data.forEach(coach => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${coach.coachId}</td>
                        <td>${coach.firstName} ${coach.lastName}</td>
                        <td>${coach.specialization}</td>
                        <td>
                            <a href="#" onclick="editCoach(${coach.coachId})" class="bttns">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                            <a href="#" onclick="deleteCoach(${coach.coachId})" class="bttns">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching coaches:', error));
}

function fetchEvents(){
    fetch('/api/events/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('events-table-body');
            tableBody.innerHTML = '';
            data.forEach(event => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${event.eventId}</td>
                    <td>${event.eventName}</td>
                    <td>${event.mmaOrganization}</td>
                    <td>${new Date(event.startOfEvent).toLocaleString()} - ${new Date(event.endOfEvent).toLocaleString()}</td>
                    <td>${event.location}</td>
                    <td>
                        <a href="#" onclick="editEvent(${event.eventId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteEvent(${event.eventId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching events:', error));
}

function fetchCategories(){
    fetch('/api/weight_categories/all')
        .then(response => response.text())
        .then(data => {
            try {
                let parsedData = JSON.parse(data);
                const tableBody = document.getElementById('weight_categories-table-body');
                tableBody.innerHTML = '';
                parsedData.forEach(category => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${category.weightCategoryId}</td>
                        <td>${category.name}</td>
                        <td>${category.minWeight} - ${category.maxWeight}</td>
                        <td>
                            <a href="#" onclick="editCategory(${category.weightCategoryId})" class="bttns">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                            <a href="#" onclick="deleteCategory(${category.weightCategoryId})" class="bttns">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
            }
            catch(error) {console.error("Error parsing JSON:", error);}
        })
        .catch(error => console.error('Error fetching weight categories:', error));
}

function fetchMMAFighters(){
    fetch('/api/mma-fighters/all')
        .then(response => response.text())
        .then(data => {
            try {
                const fighters = JSON.parse(data);
                const tableBody = document.getElementById('mma_fighters-table-body');
                tableBody.innerHTML = '';
                fighters.forEach(fighter =>
                {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${fighter.fighterId}</td>
                        <td>${fighter.first_name} ${fighter.last_name}</td>
                        <td>${fighter.weight}</td>
                        <td>${fighter.height}</td>
                        <td>${fighter.reach}</td>
                        <td>${fighter.nationality}</td>
                        <td>${fighter.ranking}</td>
                        <td>${fighter.fights}</td>
                        <td>${fighter.points}</td>
                        <td>${fighter.weight_category_id.name || 'N/A'}</td>
                        <td>
                            <a href="#" onclick="editFighter(${fighter.fighterId})" class="bttns">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                            <a href="#" onclick="deleteFighter(${fighter.fighterId})" class="bttns">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
            }
            catch(error) {console.error("Error parsing JSON:", error);}
        })
        .catch(error => console.error('Error fetching MMA fighters:', error));
}

function fetchStats(){
    fetch('/api/stats/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('stats-table-body');
            tableBody.innerHTML = '';
            data.forEach(stats => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${stats.statsId}</td>
                        <td>${stats.wins}</td>
                        <td>${stats.losses}</td>
                        <td>${stats.draws}</td>
                        <td>${stats.kos}</td>
                        <td>${stats.tkos}</td>
                        <td>${stats.submissions}</td>
                        <td>${stats.decisions}</td>
                        <td>${stats['mma-fighter']?.first_name} ${stats['mma-fighter']?.last_name || 'N/A'}</td>
                        <td>${stats.decisions}</td>
                    `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching stats:', error));
}

function fetchPreparations(){
    fetch('/api/preparations/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('preparations-table-body');
            tableBody.innerHTML = '';
            data.forEach(preparation => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${preparation.preparationId}</td>
                    <td>${new Date(preparation.start_of_preparation).toLocaleString()} - ${new Date(preparation.end_of_preparation).toLocaleString()}</td>
                    <td>${preparation.mma_club}</td>
                    <td>${preparation.club_region}</td>
                    <td>${preparation['mma-fighter']?.first_name} ${preparation['mma-fighter']?.last_name || 'N/A'}</td>
                    <td>${(preparation.coach.firstName && preparation.coach.lastName) ? preparation.coach.firstName + ' ' + preparation.coach.lastName : 'N/A'}</td>
                    <td>
                        <a href="#" onclick="editPreparation(${preparation.preparationId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deletePreparation(${preparation.preparationId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching preparations:', error));
}

function fetchFights(){
    fetch('/api/fights/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('fights-table-body');
            tableBody.innerHTML = '';
            data.forEach(fight => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${fight.fightId}</td>
                    <td>${new Date(fight.date).toLocaleString()}</td>
                    <td>${fight.result}</td>
                    <td>${fight.type_of_result}</td>
                    <td>${fight.weight_category.name || 'N/A'}</td>
                    <td>${fight.event.eventName || 'N/A'}</td>
                    <td>${fight.event.location || 'N/A'}</td>
                    <td>
                        <a href="#" onclick="editFight(${fight.fightId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteFight(${fight.fightId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching fights:', error));
}

function fetchMMAFights(){
    fetch('/api/mmafights/all')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('mma-fights-table-body');
            tableBody.innerHTML = '';
            data.forEach(mmaFight => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${mmaFight.mmaFightId}</td>
                    <td>${mmaFight.fighter.fighterId}</td>
                    <td>${mmaFight.fighter.first_name} ${mmaFight.fighter.last_name}</td>
                    <td>${mmaFight.fight.weight_category?.name || 'N/A'}</td>
                    <td>
                        <a href="#" onclick="editMMAFight(${mmaFight.mmaFightId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteMMAFight(${mmaFight.mmaFightId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching MMAFights:', error));
}