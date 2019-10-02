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
    if (data && method != "GET") {
        data = JSON.stringify(data);
    }
    const userInfo = getCookie("userInfo");
    let tokenAutenticacao = null;
    if (userInfo) {
        tokenAutenticacao = userInfo.token;
    }
    $.ajax({
        url: service,
        type: method,
        data: data,
        dataType: 'json',
        contentType: "application/json",
        headers: tokenAutenticacao ? { "Authorization": "Bearer " + tokenAutenticacao } : null,
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
            return JSON.parse(c.substring(name.length, c.length));
        }
    }
    return "";
}

function salvarToken(token) {
    setCookie("userInfo", JSON.stringify(token), new Date(token.dataExpiracao));
}

function obterToken() {
    return JSON.parse(getCookie("userInfo"));
}

function getURLParameter(sParam) {
    let sPageURL = window.location.search.substring(1);
    let sURLVariables = sPageURL.split('&');
    for (let i = 0; i < sURLVariables.length; i++) {
        let sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}