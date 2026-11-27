// tela cadastre-filme.html: POST /uel/filmes (RF4)
// uma unica transacao no service: filme + avaliacao + questoes do bloco
// + destinatarios para todos os usuarios
import { apiJson, mostrarAlerta } from './app.js';
async function iniciar() {
    // popula o select de generos
    const generos = await apiJson('/generos');
    document.getElementById('generoId').innerHTML =
        '<option value="">Selecione...</option>'
            + generos.map((g) => `<option value="${g.id}">${g.nome}</option>`).join('');
}
const form = document.querySelector('#form-filme');
form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
        const corpo = {
            titulo: document.getElementById('titulo').value,
            ano: Number(document.getElementById('ano').value),
            generoId: Number(document.getElementById('generoId').value),
            duracaoMin: document.getElementById('duracaoMin').value
                ? Number(document.getElementById('duracaoMin').value) : null,
            sinopse: document.getElementById('sinopse').value,
        };
        await apiJson('/filmes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo),
        });
        window.location.href = 'catalogo.html';
    }
    catch (erro) {
        // 409 do controller: UNIQUE(titulo, ano) -> "Esse filme ja esta no catalogo"
        mostrarAlerta('alerta', erro.message);
    }
});
iniciar().catch((erro) => mostrarAlerta('alerta', erro.message));
