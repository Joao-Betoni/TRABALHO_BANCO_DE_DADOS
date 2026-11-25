// tela relatorios.html: os 5 relatorios (RF7), cada um tabela + grafico.
// consume GET /uel/relatorios/... cujo SQL espelha database/relatorios.sql
import { apiJson, mostrarAlerta } from './app.js';
const CORES = ['#0d6efd', '#dc3545', '#198754', '#ffc107', '#6f42c1', '#20c997'];
// desenha grafico de barras (ou linhas) + tabela de pares rotulo/valor
function desenharBarras(canvasId, rotulos, valores, horizontal = false) {
    new Chart(document.getElementById(canvasId), {
        type: 'bar',
        data: {
            labels: rotulos,
            datasets: [{ data: valores, backgroundColor: CORES[0] }],
        },
        options: {
            indexAxis: horizontal ? 'y' : 'x',
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } },
        },
    });
}
function desenharTabela(elId, cabecalho, linhas) {
    document.getElementById(elId).innerHTML = `
        <thead><tr>${cabecalho.map((c) => `<th>${c}</th>`).join('')}</tr></thead>
        <tbody>${linhas.map((l) => `<tr>${l.map((c) => `<td>${c}</td>`).join('')}</tr>`).join('')}</tbody>`;
}
async function carregarR1() {
    const linhas = await apiJson('/relatorios/temas-genero');
    const generos = [...new Set(linhas.map((l) => l.genero))];
    const temas = [...new Set(linhas.map((l) => l.tema))];
    const datasets = temas.map((tema, i) => ({
        label: tema,
        data: generos.map((g) => linhas.find((l) => l.genero === g && l.tema === tema)?.media ?? null),
        backgroundColor: CORES[i % CORES.length],
    }));
    new Chart(document.getElementById('grafico-r1'), {
        type: 'bar',
        data: { labels: generos, datasets },
        options: { scales: { y: { beginAtZero: true, max: 5 } } },
    });
    desenharTabela('tabela-r1', ['Gênero', 'Tema', 'Média', 'Respostas'], linhas.map((l) => [l.genero, l.tema, l.media.toFixed(2), String(l.respostas)]));
}
async function carregarR2() {
    const linhas = await apiJson('/relatorios/ranking');
    desenharBarras('grafico-r2', linhas.map((l) => `${l.titulo} (${l.ano})`), linhas.map((l) => l.media), true);
    desenharTabela('tabela-r2', ['Filme', 'Média', 'Avaliadores'], linhas.map((l) => [`${l.titulo} (${l.ano})`, l.media.toFixed(2), String(l.avaliadores)]));
}
async function carregarR3() {
    const linhas = await apiJson('/relatorios/sem-avaliacao');
    desenharBarras('grafico-r3', linhas.map((l) => l.titulo), linhas.map((l) => l.respostas));
    desenharTabela('tabela-r3', ['Filme', 'Avaliadores', 'Respostas'], linhas.map((l) => [l.titulo, String(l.avaliadores), String(l.respostas)]));
}
async function carregarR4() {
    const linhas = await apiJson('/relatorios/evolucao');
    const temas = [...new Set(linhas.map((l) => l.tema))];
    const filtro = document.getElementById('filtro-tema-r4');
    filtro.innerHTML = temas.map((t) => `<option>${t}</option>`).join('');
    function render() {
        const temaSel = filtro.value;
        const doTema = linhas.filter((l) => l.tema === temaSel);
        const semanas = [...new Set(doTema.map((l) => l.semana))].sort();
        const grupos = [...new Set(doTema.map((l) => l.grupo))];
        new Chart(document.getElementById('grafico-r4'), {
            type: 'line',
            data: {
                labels: semanas,
                datasets: grupos.map((g, i) => ({
                    label: g,
                    data: semanas.map((semana) => doTema.find((l) => l.semana === semana && l.grupo === g)?.media ?? null),
                    borderColor: CORES[i % CORES.length],
                    backgroundColor: CORES[i % CORES.length],
                })),
            },
            options: { scales: { y: { beginAtZero: true, max: 5 } } },
        });
        desenharTabela('tabela-r4', ['Semana', 'Grupo', 'Média', 'Respostas'], doTema.map((l) => [l.semana, l.grupo, l.media.toFixed(2), String(l.respostas)]));
    }
    filtro.addEventListener('change', render);
    render();
}
async function carregarR5() {
    const linhas = await apiJson('/relatorios/criticados');
    desenharBarras('grafico-r5', linhas.map((l) => l.tema), linhas.map((l) => l.pctCriticadas));
    desenharTabela('tabela-r5', ['Tema', 'Média', '% notas 1-2', 'Respostas'], linhas.map((l) => [l.tema, l.media.toFixed(2), `${l.pctCriticadas}%`, String(l.respostas)]));
}
carregarR1().catch((e) => mostrarAlerta('alerta', `R1: ${e.message}`));
carregarR2().catch((e) => mostrarAlerta('alerta', `R2: ${e.message}`));
carregarR3().catch((e) => mostrarAlerta('alerta', `R3: ${e.message}`));
carregarR4().catch((e) => mostrarAlerta('alerta', `R4: ${e.message}`));
carregarR5().catch((e) => mostrarAlerta('alerta', `R5: ${e.message}`));
