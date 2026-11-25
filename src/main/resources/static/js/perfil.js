// tela perfil.html: perfil publico com os filmes avaliados (RF5)
// as avaliacoes publicadas como anonimas NAO aparecem aqui (RF6)
import { apiJson, exigirSessao, mostrarAlerta } from './app.js';
// perfil.html?id=alguem -> perfil de outra pessoa; sem id -> o do usuario logado
const params = new URLSearchParams(window.location.search);
const alvoPerfil = params.get('id') ?? '';
async function iniciar() {
    // sem ?id= na URL, mostra o perfil do proprio usuario logado
    const nome = alvoPerfil || (await exigirSessao()).nomePerfil;
    const perfil = await apiJson(`/perfil/${encodeURIComponent(nome)}`);
    document.getElementById('perfil-nome').textContent = perfil.nome;
    document.getElementById('perfil-usuario').textContent = `@${perfil.nomePerfil}`;
    document.getElementById('lista-avaliacoes').innerHTML = perfil.avaliacoes.map((a) => `
        <div class="list-group-item d-flex justify-content-between align-items-center">
            <span>${a.titulo} <span class="text-body-secondary">(${a.ano})</span></span>
            <span class="badge bg-warning text-dark">${a.nota ? '⭐ '.repeat(a.nota) : 'sem nota'}</span>
        </div>`).join('')
        || '<p class="text-body-secondary mt-2">Nenhuma avaliação pública ainda.</p>';
}
iniciar().catch((erro) => mostrarAlerta('alerta', erro.message));
