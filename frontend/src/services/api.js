import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

function criarServico(recurso) {
  return {
    listar: async () => (await api.get(recurso)).data,
    buscar: async (id) => (await api.get(`${recurso}/${id}`)).data,
    criar: async (dados) => (await api.post(recurso, dados)).data,
    atualizar: async (id, dados) => (await api.put(`${recurso}/${id}`, dados)).data,
    deletar: async (id) => api.delete(`${recurso}/${id}`),
  };
}

export const autorService = criarServico('/autores');

export function mensagemDeErro(erro, mensagemPadrao) {
  return erro.response?.data?.detail || mensagemPadrao;
}
