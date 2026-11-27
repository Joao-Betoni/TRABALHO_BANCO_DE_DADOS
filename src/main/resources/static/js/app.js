// HELPERS DAS TELAS DO FRONT-END
const UEL = '/uel'; // prefixo unico das rotas (UEL = campus do projeto)
// mensagem de erro devolvida pelo controller
export async function apiJson(url, opcoes = {}) {
    const resposta = await fetch(UEL + url, opcoes);
    const texto = await resposta.text();
    // corpo pode ser JSON, texto puro (erros dos controllers) ou vazio (201
    // do POST de respostas - antes isso lancava "Unexpected end of JSON input")
    let corpo = undefined;
    if (texto !== '') {
        try {
            corpo = JSON.parse(texto);
        }
        catch {
            corpo = texto;
        }
    }
    if (!resposta.ok) {
        const obj = corpo;
        const mensagem = obj?.message
            ?? (typeof corpo === 'string' ? corpo : `${resposta.status}`);
        throw new Error(mensagem);
    }
    return (corpo ?? undefined);
}
// toda tela começa exigindo a sessão, caso contrário ele retorna que não possui
export async function exigirSessao() {
    const resposta = await fetch(UEL + '/sessao');
    if (!resposta.ok) {
        window.location.href = 'index.html';
        throw new Error('Sem sessão');
    }
    return resposta.json();
}
//Alerta
export function mostrarAlerta(id, mensagem) {
    const alerta = document.getElementById(id);
    alerta.textContent = mensagem;
    alerta.classList.remove('d-none');
}
