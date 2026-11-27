// tela perfil.html: perfil com as AVALIACOES DETALHADAS (respostas por tema)
// - no perfil propio: publicas + anonimas dele + pendentes (RF5 e RF6)
// - no perfil dos outros: somente as publicas (RF6: anonima nao acusa ninguem)
import { apiJson, exigirSessao, mostrarAlerta } from './app.js';

// perfil.html?id=alguem -> perfil de outra pessoa; sem id -> o do usuario logado
const params = new URLSearchParams(window.location.search);
const alvoPerfil = params.get('id') ?? '';

// contrato do GET /uel/avaliacoes/usuario/{nomePerfil}:
// [{ avaliacaoId, filmeId, titulo, ano, enviadaEm, anonima,
//    respostas: [{ tema, enunciado, tipo, nota, alternativaTexto, texto }] }]
// contrato do GET /uel/avaliacoes/me (so no perfil propio, p/ os pendentes):
// [{ avaliacaoId, filmeId, titulo, ano, avaliacaoUsuarioId, enviadaEm, anonima }] —
// avaliacaoUsuarioId nulo = ainda não respondeu

function estrelas(nota) {
    return '★'.repeat(nota) + '☆'.repeat(5 - nota);
}

function dataCurta(iso) {
    return iso ? new Date(iso).toLocaleDateString('pt-BR') : '—';
}

function badgeSituacao(anonima) {
    return anonima
        ? '<span class="badge text-bg-dark">🔒 Anônima</span>'
        : '<span class="badge text-bg-success">Pública</span>';
}

// uma linha de resposta dentro do corpo do accordion
function itemResposta(r) {
    let valor;
    if (r.tipo === 'ESCOLHA' && r.alternativaTexto) valor = r.alternativaTexto;
    else if (r.tipo === 'TEXTO' && r.texto) valor = `“${r.texto}”`;
    else if (r.nota !== null && r.nota !== undefined) valor = estrelas(r.nota);
    else valor = '(sem resposta)';
    return `
        <div class="mb-2">
            <span class="badge text-bg-secondary me-1">${r.tema}</span>
            ${r.enunciado}
            <div class="ms-2">→ <strong>${valor}</strong></div>
        </div>`;
}

// um item de accordion por filme (avaliado ou pendente)
function itemAccordion(avaliacao, index) {
    const id = `avaliacao-${index}`;
    const corpo = (avaliacao.respostas ?? []).map(itemResposta).join('')
        || '<p class="text-body-secondary mb-0">Ainda sem respostas.</p>';
    const pendente = avaliacao.pendente
        ? `<a href="filme.html?id=${avaliacao.filmeId}" class="ms-2 small">avaliar agora</a>`
        : '';
    return `
        <div class="accordion-item">
            <h2 class="accordion-header">
                <button class="accordion-button collapsed" type="button"
                        data-bs-toggle="collapse" data-bs-target="#${id}">
                    <span class="me-auto">${avaliacao.titulo}
                        <span class="text-body-secondary">(${avaliacao.ano})</span></span>
                    ${avaliacao.badge}
                    <span class="text-body-secondary ms-3 small">${dataCurta(avaliacao.enviadaEm)}</span>
                </button>
            </h2>
            <div id="${id}" class="accordion-collapse collapse" data-bs-parent="#accordion-avaliacoes">
                <div class="accordion-body">${corpo}${pendente}</div>
            </div>
        </div>`;
}

async function iniciar() {
    // sem ?id= na URL, mostra o perfil do proprio usuario logado
    const nome = alvoPerfil || (await exigirSessao()).nomePerfil;
    const perfil = await apiJson(`/perfil/${encodeURIComponent(nome)}`);
    document.getElementById('perfil-nome').textContent = perfil.nome;
    document.getElementById('perfil-usuario').textContent = `@${perfil.nomePerfil}`;
    document.getElementById('titulo-avaliacoes').textContent =
        'Avaliações de ' + perfil.nome;

    // detalhadas: publicas sempre; anonimas so quando sessao == perfil (RF6)
    const avaliacoes = await apiJson(`/avaliacoes/usuario/${encodeURIComponent(nome)}`);
    const itens = (avaliacoes ?? []).map((a) => ({
        titulo: a.titulo,
        ano: a.ano,
        enviadaEm: a.enviadaEm,
        badge: badgeSituacao(a.anonima),
        respostas: a.respostas,
    }));

    // no perfil propio, os instrumentos ainda sem resposta entram como
    // itens "Pendente" com link direto pro questionario
    if (!alvoPerfil) {
        const minhas = await apiJson('/avaliacoes/me');
        const respondidos = new Set((avaliacoes ?? []).map((a) => a.avaliacaoId));
        for (const m of (minhas ?? [])) {
            const feita = (m.avaliacaoUsuarioId !== null && m.avaliacaoUsuarioId !== undefined)
                || respondidos.has(m.avaliacaoId);
            if (!feita) {
                itens.push({
                    titulo: m.titulo,
                    ano: m.ano,
                    enviadaEm: null,
                    badge: '<span class="badge text-bg-light border">⏳ Pendente</span>',
                    respostas: [],
                    pendente: true,
                    filmeId: m.filmeId,
                });
            }
        }
    }

    document.getElementById('accordion-avaliacoes').innerHTML =
        (itens.length ? itens.map(itemAccordion).join('')
            : '<p class="text-body-secondary mb-0">Nenhuma avaliação pública ainda.</p>');
}
iniciar().catch((erro) => mostrarAlerta('alerta', erro.message));
