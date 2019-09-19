$(document).ready(() => {
    carregarNav();
});

function carregarNav(){
    let nav = $(".triviaNav");
    if(nav){
        $(nav).load("/nav.html");
    }
}