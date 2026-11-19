import { useParams } from 'react-router-dom';
import { autorService } from '../services/api';
import FormCadastro from './FormCadastro';

const campos = [
  { nome: 'nome', rotulo: 'Nome', required: true, maxLength: 100, larguraTotal: true },
  { nome: 'nacionalidade', rotulo: 'Nacionalidade', maxLength: 50 },
  { nome: 'dataNascimento', rotulo: 'Data de nascimento', type: 'date' },
];

function ler(autor) {
  return {
    nome: autor.nome ?? '',
    nacionalidade: autor.nacionalidade ?? '',
    dataNascimento: autor.dataNascimento ?? '',
  };
}

function preparar(autor) {
  return {
    nome: autor.nome.trim(),
    nacionalidade: autor.nacionalidade.trim() || null,
    dataNascimento: autor.dataNascimento || null,
  };
}

export default function AutorForm() {
  const { id } = useParams();
  return (
    <FormCadastro
      key={id ?? 'novo'}
      singular="autor"
      plural="autores"
      rota="/autores"
      servico={autorService}
      campos={campos}
      ler={ler}
      preparar={preparar}
    />
  );
}
