// tela catalogo.html: catálogo de filmes brasileiros com filtro (RF5)
import { apiJson, exigirSessao, mostrarAlerta } from './app.js';
let generos = [];
let filmes = [];
let avaliados = new Set(); // chave "titulo|ano"
async function carregarAvaliados() {
    try {
        const minhas = await apiJson('/avaliacoes/me');
        avaliados = new Set((minhas ?? [])
            .filter((a) => a.avaliacaoUsuarioId !== null)
            .map((a) => `${a.titulo}|${a.ano}`));
    }
    catch {
        avaliados = new Set();
    } // sem a lista, o catalogo funciona igual
}
function generoNome(id) {
    return generos.find((g) => g.id === id)?.nome ?? '—';
}
function cartaoFilme(filme) {
    const duracao = filme.duracaoMin ? `· ${filme.duracaoMin} min` : '';
    const jaAvaliado = avaliados.has(`${filme.titulo}|${filme.ano}`);
    const badge = jaAvaliado
        ? '<span class="badge text-bg-success">✅ avaliado</span>' : '';
    return `
        <div class="col">
            <div class="card h-100 shadow-sm">
                <div class="card-body">
                    <h2 class="h5 card-title">${filme.titulo} <span class="text-body-secondary">(${filme.ano})</span> ${badge}</h2>
                    <p class="card-subtitle mb-2 text-body-secondary">${generoNome(filme.generoId)} ${duracao}</p>
                    <p class="card-text">${filme.sinopse ?? ''}</p>
                </div>
                <div class="card-footer bg-transparent">
                    <a href="filme.html?id=${filme.id}" class="card-link">${jaAvaliado ? 'Ver respostas' : 'Avaliar'}</a>
                </div>
            </div>
        </div>`;
}
function render(filmesVisiveis) {
    document.getElementById('grade-filmes').innerHTML =
        filmesVisiveis.map(cartaoFilme).join('') || '<p class="text-body-secondary">Nenhum filme encontrado.</p>';
}
// filtro vai pro SERVIDOR (?genero=&busca=) - o SQL tem que resolver, nao o front
async function buscar() {
    const busca = document.getElementById('busca').value;
    const genero = document.getElementById('filtro-genero').value;
    const query = new URLSearchParams();
    if (busca)
        query.set('busca', busca);
    if (genero)
        query.set('genero', genero);
    const lista = await apiJson('/filmes?' + query.toString());
    render(lista);
}
async function iniciar() {
    await exigirSessao();
    // carrega generos (menu do filtro), filmes (grade) e o que o usuario
    // ja avaliou (badge "✅ avaliado" em vez de 400 ao clicar avaliar)
    const [generosApi, filmesApi] = await Promise.all([
        apiJson('/generos'),
        apiJson('/filmes'),
    ]);
    generos = generosApi;
    filmes = filmesApi;
    await carregarAvaliados();
    document.getElementById('filtro-genero').innerHTML =
        '<option value="">Todos os gêneros</option>'
            + generos.map((g) => `<option value="${g.id}">${g.nome}</option>`).join('');
    document.getElementById('busca').addEventListener('input', buscar);
    document.getElementById('filtro-genero').addEventListener('change', buscar);
    document.getElementById('btn-sair').addEventListener('click', async () => {
        await apiJson('/logout', { method: 'POST' });
        window.location.href = 'index.html';
    });
    render(filmes);
}
iniciar().catch((erro) => mostrarAlerta('alerta', erro.message));
