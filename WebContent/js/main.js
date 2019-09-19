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

function requestService(service, method, data, callbackSuccess, callbackFail) {
    $.ajax({
        url: service,
        type: method,
        data: data ? JSON.stringify(data) : null,
        processData: false,
        contentType: "application/json",
        success: callbackSuccess,
        error: callbackFail
    });
}

function setCookie(nome, valor, validade) {
    let expires = "expires=" + validade.toUTCString();
    document.cookie = nome + "=" + valor + ";" + expires + ";path=/";
}

function getCookie(nome) {
    var name = nome + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function salvarToken(token) {
    setCookie("userInfo", JSON.stringify(token), new Date(token.dataExpiracao));
}

function obterToken(){
    return JSON.parse(getCookie("userInfo"));
}