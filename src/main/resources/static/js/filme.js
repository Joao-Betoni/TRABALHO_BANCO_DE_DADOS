// tela filme.html?id=N: o questionario do filme, renderizado em BLOCOS POR
// TEMA (particularidade da natureza: pesquisa de opiniao agrupada - RF6)
import { apiJson, mostrarAlerta } from './app.js';
const params = new URLSearchParams(window.location.search);
const filmeId = params.get('id');
let questionario = null;
// agrupa as questoes por tema, mantendo a ordem em que aparecem no instrumento
function blocosPorTema(questoes) {
    const blocos = [];
    for (const questao of questoes) {
        let bloco = blocos.find((b) => b.tema === questao.tema);
        if (!bloco) {
            bloco = { tema: questao.tema, questoes: [] };
            blocos.push(bloco);
        }
        bloco.questoes.push(questao);
    }
    return blocos;
}
function htmlEscala(questao) {
    const opcoes = [];
    for (let nota = questao.escalaMin; nota <= questao.escalaMax; nota++) {
        opcoes.push(`
            <label class="estrela-opcao">
                <input type="radio" name="q${questao.id}" value="${nota}">
                ${nota}
            </label>`);
    }
    return opcoes.join('');
}
function htmlEscolha(questao) {
    const ops = questao.alternativas
        .map((a) => `<option value="${a.id}">${a.texto}</option>`)
        .join('');
    return `<select class="form-select" data-questao="${questao.id}">
                <option value="">Selecione...</option>${ops}
            </select>`;
}
function htmlQuestao(questao) {
    const estrela = questao.tipo === 'ESCOLHA' ? '' : '⭐ ';
    let controle = '';
    if (questao.tipo === 'ESCALA')
        controle = htmlEscala(questao);
    if (questao.tipo === 'ESCOLHA')
        controle = htmlEscolha(questao);
    if (questao.tipo === 'TEXTO') {
        controle = `<textarea class="form-control" data-questao="${questao.id}" rows="4"
                        placeholder="Escreva aqui sua crítica..."></textarea>`;
    }
    return `
        <div class="mb-4">
            <p class="mb-2">${estrela}${questao.enunciado}
                ${questao.obrigatoria ? '' : '<span class="text-body-secondary">(opcional)</span>'}</p>
            ${controle}
        </div>`;
}
function render() {
    if (!questionario)
        return;
    const container = document.getElementById('questionario');
    container.innerHTML = blocosPorTema(questionario.questoes).map((bloco) => `
        <fieldset class="border rounded-3 p-3 mb-4">
            <legend class="float-none w-auto px-2 h6">${bloco.tema}</legend>
            ${bloco.questoes.map(htmlQuestao).join('')}
        </fieldset>`).join('');
    // marca a estrela escolhida (classe .marcada do style.css)
    container.querySelectorAll('input[name^="q"]').forEach((input) => {
        input.addEventListener('change', () => {
            document.querySelectorAll(`input[name="${input.name}"]`)
                .forEach((r) => r.parentElement.classList.remove('marcada'));
            input.parentElement.classList.add('marcada');
        });
    });
}
function coletarRespostas() {
    const respostas = [];
    for (const questao of questionario.questoes) {
        if (questao.tipo === 'ESCALA') {
            const marcada = document.querySelector(`input[name="q${questao.id}"]:checked`);
            respostas.push({ questaoId: questao.id, nota: marcada ? Number(marcada.value) : null });
        }
        else if (questao.tipo === 'ESCOLHA') {
            const valor = document.querySelector(`select[data-questao="${questao.id}"]`).value;
            respostas.push({ questaoId: questao.id, alternativaId: valor ? Number(valor) : null });
        }
        else { // TEXTO
            const valor = document.querySelector(`textarea[data-questao="${questao.id}"]`).value;
            respostas.push({ questaoId: questao.id, texto: valor.trim() === '' ? null : valor });
        }
    }
    return respostas;
}
async function iniciar() {
    if (!filmeId) {
        mostrarAlerta('alerta', 'Filme não informado na URL.');
        return;
    }
    questionario = await apiJson(`/filmes/${filmeId}/questionario`);
    document.getElementById('titulo-filme').textContent = questionario.filmeTitulo;
    document.getElementById('btn-enviar').addEventListener('click', async () => {
        try {
            const form = {
                anonima: document.getElementById('anonima').checked,
                respostas: coletarRespostas(),
            };
            await apiJson(`/avaliacoes/${questionario.avaliacaoId}/respostas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(form),
            });
            window.location.href = 'catalogo.html'; // avaliado: volta pro catalogo
        }
        catch (erro) {
            mostrarAlerta('alerta', erro.message); // 400 do service
        }
    });
    render();
}
iniciar().catch((erro) => mostrarAlerta('alerta', erro.message));
