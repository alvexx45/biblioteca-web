import { autorService } from '../services/api';
import ListaCadastro from './ListaCadastro';

function dataBrasileira(data) {
  return data?.split('-').reverse().join('/');
}

const colunas = [
  { rotulo: 'Nome', valor: (autor) => autor.nome },
  { rotulo: 'Nacionalidade', valor: (autor) => autor.nacionalidade },
  { rotulo: 'Nascimento', valor: (autor) => dataBrasileira(autor.dataNascimento) },
];

export default function AutorList() {
  return (
    <ListaCadastro
      titulo="Autores"
      singular="autor"
      rota="/autores"
      servico={autorService}
      colunas={colunas}
    />
  );
}
