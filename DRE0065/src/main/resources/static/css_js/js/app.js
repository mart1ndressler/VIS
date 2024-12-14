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
    document.getElementById('addCategoryBtn').style.display = 'none';
    document.getElementById('addFightBtn').style.display = 'none';
    document.getElementById('addEventBtn').style.display = 'none';
    document.getElementById('addMMAFightBtn').style.display = 'none';
    document.getElementById('addMMAFighterBtn').style.display = 'none';
    document.getElementById('addPreparationBtn').style.display = 'none';
    document.getElementById('addStatsBtn').style.display = 'none';
}

function showCoaches(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('coaches-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addCoachBtn').style.display = 'block';
    }
    fetchCoaches();
}

function showEvents(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('events-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addEventBtn').style.display = 'block';
    }
    fetchEvents();
}

function showWeightCategories(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('weight_categories-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addCategoryBtn').style.display = 'block';
    }
    fetchCategories();
}

function showMMAFighters(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('mma_fighters-table').style.display = 'table';
    document.getElementById('mma-fighter-filters').style.display = 'block';
    if(getUserRole() === 'admin') {
        document.getElementById('addMMAFighterBtn').style.display = 'block';
    }
    fetchMMAFighters();
}

function showStats(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    fetchWins();
    document.getElementById('stats-table').style.display = 'table';
    document.getElementById('stats-filters').style.display = 'block';
    if(getUserRole() === 'admin') {
        document.getElementById('addStatsBtn').style.display = 'block';
    }
    fetchStats();
}

function showPreparations(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('preparations-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addPreparationBtn').style.display = 'block';
    }
    fetchPreparations();
}

function showFights(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('fights-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addFightBtn').style.display = 'block';
    }
    fetchFights();
}

function showMMAFights(){
    hideAllTables();
    hideFilters();
    hideCRUDBtns();
    document.getElementById('mma-fights-table').style.display = 'table';
    if(getUserRole() === 'admin') {
        document.getElementById('addMMAFightBtn').style.display = 'block';
    }
    fetchMMAFights();
}

function fetchCoaches(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/coaches/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('coaches-table-body');
            if(!tableBody)
            {
                console.error('Table body for coaches not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(coach =>
            {
                const row = document.createElement('tr');

                row.innerHTML = `
                    <td>${coach.coachId}</td>
                    <td>${coach.firstName} ${coach.lastName}</td>
                    <td>${coach.specialization}</td>
                `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editCoach(${coach.coachId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteCoachHandler(${coach.coachId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching coaches:', error));
}

function fetchEvents(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/events/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('events-table-body');
            if(!tableBody)
            {
                console.error('Table body for events not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(event =>
            {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${event.eventId}</td>
                    <td>${event.eventName}</td>
                    <td>${event.mmaOrganization}</td>
                    <td>${new Date(event.startOfEvent).toLocaleString()} - ${new Date(event.endOfEvent).toLocaleString()}</td>
                    <td>${event.location}</td>
                `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editEvent(${event.eventId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteEvent(${event.eventId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching events:', error));
}

function fetchCategories(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/weight_categories/all')
        .then(response => response.text())
        .then(data =>
        {
            try
            {
                let parsedData = JSON.parse(data);
                const tableBody = document.getElementById('weight_categories-table-body');
                if(!tableBody)
                {
                    console.error('Table body for weight categories not found.');
                    return;
                }
                tableBody.innerHTML = '';
                parsedData.forEach(category =>
                {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${category.weightCategoryId}</td>
                        <td>${category.name}</td>
                        <td>${category.minWeight} - ${category.maxWeight}</td>
                    `;

                    if(userRole === 'admin')
                    {
                        const actionTd = document.createElement('td');
                        actionTd.innerHTML = `
                            <a href="#" onclick="editCategory(${category.weightCategoryId})" class="bttns">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                            <a href="#" onclick="deleteCategory(${category.weightCategoryId})" class="bttns">
                                <i class="bi bi-trash"></i>
                            </a>
                        `;
                        row.appendChild(actionTd);
                    }
                    tableBody.appendChild(row);
                });
            }
            catch(error) {console.error("Error parsing JSON:", error);}
        })
        .catch(error => console.error('Error fetching weight categories:', error));
}

function fetchMMAFighters(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/mma-fighters/all')
        .then(response => response.json())
        .then(fighters =>
        {
            const tableBody = document.getElementById('mma_fighters-table-body');
            if(!tableBody)
            {
                console.error('Table body for MMA fighters not found.');
                return;
            }
            tableBody.innerHTML = '';
            fighters.forEach(fighter =>
            {
                const userRole = getUserRole();
                let actions = `
                    <a href="#" onclick="viewMMAFighter(${fighter.fighterId}, '${fighter.first_name}', '${fighter.last_name}', '${fighter.nationality}', '${fighter.weight_category_id.name}')" class="bttns">
                        <i class="bi bi-search"></i>
                    </a>
                `;

                if(userRole === 'admin')
                {
                    actions += `
                        <a href="#" onclick="editMMAFighter(${fighter.fighterId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteMMAFighter(${fighter.fighterId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                }
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
                        ${actions}
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching MMA fighters:', error));
}

function viewMMAFighter(fighterId, firstName, lastName, nationality, weightCategoryName){
    const imagePath = `css_js/images/${fighterId}.png`;
    const fighterImage = document.getElementById('fighter-image');
    const fighterFullname = document.getElementById('fighter-fullname');
    const fighterInfo = document.getElementById('fighter-info');
    const fighterStats = document.getElementById('fighter-stats');

    fighterImage.src = imagePath;
    fighterImage.alt = `${firstName} ${lastName}`;
    fighterFullname.innerText = `${firstName} ${lastName}`;
    fighterInfo.innerText = `${nationality} - ${weightCategoryName}`;

    fighterImage.onerror = function()
    {
        this.onerror = null;
        this.src = '/css_js/images/logo.png';
    };

    fighterStats.innerText = '';

    fetch('/api/stats/all')
        .then(response => response.json())
        .then(statsArray =>
        {
            const fighterStat = statsArray.find(stat => stat["mma-fighter"].first_name.toLowerCase() === firstName.toLowerCase() && stat["mma-fighter"].last_name.toLowerCase() === lastName.toLowerCase());
            if(fighterStat) fighterStats.innerText = `${fighterStat.wins} - ${fighterStat.losses} - ${fighterStat.draws}`;
            else fighterStats.innerText = 'No stats available.';
        })
        .catch(error =>
        {
            console.error('Error fetching stats:', error);
            fighterStats.innerText = 'Error loading stats.';
        });
    $('#fighterModal').modal('show');
}

function fetchStats(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/stats/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('stats-table-body');
            if(!tableBody)
            {
                console.error('Table body for stats not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(stats =>
            {
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
                    `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editStat(${stats.statsId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteStat(${stats.statsId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching stats:', error));
}

function fetchPreparations(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/preparations/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('preparations-table-body');
            if(!tableBody)
            {
                console.error('Table body for preparations not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(preparation =>
            {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${preparation.preparationId}</td>
                    <td>${new Date(preparation.start_of_preparation).toLocaleString()} - ${new Date(preparation.end_of_preparation).toLocaleString()}</td>
                    <td>${preparation.mma_club}</td>
                    <td>${preparation.club_region}</td>
                    <td>${preparation['mma-fighter']?.first_name} ${preparation['mma-fighter']?.last_name || 'N/A'}</td>
                    <td>${(preparation.coach.firstName && preparation.coach.lastName) ? preparation.coach.firstName + ' ' + preparation.coach.lastName : 'N/A'}</td>
                `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editPreparation(${preparation.preparationId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deletePreparation(${preparation.preparationId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching preparations:', error));
}

function fetchFights(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/fights/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('fights-table-body');
            if(!tableBody)
            {
                console.error('Table body for fights not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(fight =>
            {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${fight.fightId}</td>
                    <td>${new Date(fight.date).toLocaleString()}</td>
                    <td>${fight.result}</td>
                    <td>${fight.type_of_result}</td>
                    <td>${fight.weight_category.name || 'N/A'}</td>
                    <td>${fight.event.eventName || 'N/A'}</td>
                    <td>${fight.event.location || 'N/A'}</td>
                `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editFight(${fight.fightId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteFight(${fight.fightId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }

                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching fights:', error));
}

function fetchMMAFights(){
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    fetch('/api/mmafights/all')
        .then(response => response.json())
        .then(data =>
        {
            const tableBody = document.getElementById('mma-fights-table-body');
            if(!tableBody)
            {
                console.error('Table body for MMA fights not found.');
                return;
            }
            tableBody.innerHTML = '';
            data.forEach(mmaFight =>
            {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${mmaFight.mmaFightId}</td>
                    <td>${mmaFight.fight.fightId}</td>
                    <td>${mmaFight.fighter.first_name} ${mmaFight.fighter.last_name}</td>
                    <td>${mmaFight.fight.weight_category?.name || 'N/A'}</td>
                `;

                if(userRole === 'admin')
                {
                    const actionTd = document.createElement('td');
                    actionTd.innerHTML = `
                        <a href="#" onclick="editMMAFight(${mmaFight.mmaFightId})" class="bttns">
                            <i class="bi bi-pencil-square"></i>
                        </a>
                        <a href="#" onclick="deleteMMAFight(${mmaFight.mmaFightId})" class="bttns">
                            <i class="bi bi-trash"></i>
                        </a>
                    `;
                    row.appendChild(actionTd);
                }
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching MMAFights:', error));
}

function getUserRole(){
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? user.role : null;
}