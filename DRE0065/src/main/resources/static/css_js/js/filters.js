function generateUniqueNationalities(){
    fetch('/api/mma-fighters/unique-nationalities')
        .then(response => response.json())
        .then(nationalities =>
        {
            const nationalitySelect = document.getElementById('nationality-filter');
            const allNationsOption = document.createElement('option');
            allNationsOption.value = "";
            allNationsOption.textContent = "All Nations";
            nationalitySelect.appendChild(allNationsOption);

            nationalities.forEach(nationality =>
            {
                const option = document.createElement('option');
                option.value = nationality;
                option.textContent = nationality;
                nationalitySelect.appendChild(option);
            });
        })
        .catch(error => {console.error('Error fetching unique nationalities:', error);});
}
window.onload = generateUniqueNationalities;

document.addEventListener('DOMContentLoaded', function(){
    fetch('/api/weight_categories/all')
        .then(response => response.json())
        .then(data =>
        {
            const selectElement = document.getElementById('weight-category-filter');
            data.forEach(category =>
            {
                const option = document.createElement('option');
                option.value = category.name;
                option.textContent = category.name;
                selectElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error loading weight categories:', error));
});

function applyMMAFighterFilter(){
    const weightCategory = document.getElementById('weight-category-filter').value;
    const nationality = document.getElementById('nationality-filter').value;
    const name = document.getElementById('name-filter').value.toLowerCase();

    const tableBody = document.getElementById('mma_fighters-table-body');
    const rows = tableBody.getElementsByTagName('tr');

    for(let i=0; i < rows.length; i++)
    {
        let row = rows[i];
        let columns = row.getElementsByTagName('td');

        const fighterName = columns[1].textContent.toLowerCase();
        const fighterNationality = columns[5].textContent.toLowerCase();
        const fighterWeight = columns[9].textContent.toLowerCase();

        let match = true;
        if(weightCategory && fighterWeight !== weightCategory.toLowerCase()) match = false;
        if(nationality && fighterNationality !== nationality.toLowerCase()) match = false;
        if(name && !fighterName.includes(name)) match = false;
        row.style.display = match ? '' : 'none';
    }
}

function checkEnter(event){
    if(event.key === "Enter") {event.preventDefault(); applyMMAFighterFilter();}
}

function applyStatsFilter(){
    const minWins = document.getElementById('min-wins').value;
    const maxWins = document.getElementById('max-wins').value;

    let filteredStats = statsData;
    if(minWins !== '') filteredStats = filteredStats.filter(stat => stat.wins >= parseInt(minWins));
    if(maxWins !== '') filteredStats = filteredStats.filter(stat => stat.wins <= parseInt(maxWins));
    displayStats(filteredStats);
}

function displayStats(filteredStats){
    const tableBody = document.getElementById('stats-table-body');
    if(!tableBody) return
    const storedUser = localStorage.getItem('user');
    const user = storedUser ? JSON.parse(storedUser) : { role: 'basic' };
    const userRole = user.role;

    tableBody.innerHTML = '';
    filteredStats.forEach(stats =>
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
}

function fetchWins(){
    fetch('/api/stats/all')
        .then(response => response.json())
        .then(data =>
        {
            statsData = data;
            displayStats(statsData);
        })
        .catch(error => console.error('Error fetching stats:', error));
}