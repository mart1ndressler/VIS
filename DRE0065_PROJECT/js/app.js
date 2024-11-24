function toggleInfo() 
{
    var infos = document.getElementsByClassName('additional-info'); 
    for(var i = 0; i < infos.length; i++) infos[i].style.display = infos[i].style.display === 'none' ? 'block' : 'none';
}