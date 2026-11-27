// tela usuarios.html: seção pública da comunidade (RF6) - lista os perfis
// cadastrados (nome, @perfil, categoria) e leva ao perfil de cada um.
// Os dados públicos vêm de GET /uel/usuarios (o DAO só expõe esses 3 campos).
import { apiJson, mostrarAlerta } from './app.js';

// contrato do GET /uel/usuarios (mapas do usuarioDao.listarPerfisPublicos):
// [{ nome, nomePerfil, categoria }]

const BADGE = {
    ESPECTADOR: 'text-bg-secondary',
    CINEFILO:   'text-bg-info',
    CRITICO:    'text-bg-warning',
};

function cartaoUsuario(u) {
    const badge = BADGE[u.categoria] ?? 'text-bg-secondary';
    return `
        <div class="col">
            <div class="card h-100 shadow-sm">
                <div class="card-body">
                    <h2 class="h6 card-title mb-1">${u.nome}</h2>
                    <p class="card-subtitle text-body-secondary mb-2">@${u.nomePerfil}</p>
                    <span class="badge ${badge}">${u.categoria}</span>
                </div>
                <div class="card-footer bg-transparent">
                    <a href="perfil.html?id=${encodeURIComponent(u.nomePerfil)}" class="card-link">
                        Ver avaliações
                    </a>
                </div>
            </div>
        </div>`;
}

async function iniciar() {
    const perfis = await apiJson('/usuarios');
    document.getElementById('grade-usuarios').innerHTML =
        perfis.map(cartaoUsuario).join('')
        || '<p class="text-body-secondary">Nenhum usuário ainda.</p>';
}

iniciar().catch((erro) => mostrarAlerta('alerta', (erro).message));
