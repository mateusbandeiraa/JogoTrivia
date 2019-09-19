$(document).ready(() => {
    carregarNav();
});

function carregarNav() {
    let nav = $(".triviaNav");
    if (nav) {
        $(nav).load("/nav.html");
    }
}
function formToJson(form) {
    let json = {};
    for (let pair of $(form).serializeArray()) {
        json[pair.name] = pair.value;
    }
    return json;
}

function requestService(service, method, data, callbackSuccess, callbackFail){
    $.ajax({
        url: service,
        type: method,
        data: JSON.stringify(data),
        processData: false,
        contentType: "application/json",
        success: callbackSuccess,
        error: callbackFail
      });
}