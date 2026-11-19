import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { mensagemDeErro } from '../services/api';

export default function ListaCadastro({ titulo, singular, rota, servico, colunas }) {
  const [registros, setRegistros] = useState(null);
  const [erro, setErro] = useState('');
  const [excluindo, setExcluindo] = useState(null);

  useEffect(() => {
    let ativo = true;
    servico.listar().then((dados) => {
      if (ativo) setRegistros(dados);
    }).catch((falha) => {
      if (ativo) setErro(mensagemDeErro(falha, 'Não foi possível carregar o cadastro.'));
    });
    return () => { ativo = false; };
  }, [servico]);

  async function excluir(id, nome) {
    if (!window.confirm(`Excluir "${nome}"?`)) return;

    setExcluindo(id);
    setErro('');
    try {
      await servico.deletar(id);
      setRegistros((atuais) => atuais.filter((registro) => registro.id !== id));
    } catch (falha) {
      setErro(mensagemDeErro(falha, 'Não foi possível excluir este registro.'));
    } finally {
      setExcluindo(null);
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>{titulo}</h1>
          {registros && (
            <p className="secondary-text">
              {registros.length} {registros.length === 1 ? 'registro' : 'registros'}
            </p>
          )}
        </div>
        <Link className="button primary" to={`${rota}/novo`}>Novo {singular}</Link>
      </div>
      {erro && <p className="notice error" role="alert">{erro}</p>}
      {!registros && !erro && <p role="status">Carregando…</p>}
      {registros && (
        <div className="panel table-panel">
          <table aria-label={titulo}>
            <thead>
              <tr>
                {colunas.map(({ rotulo }) => <th key={rotulo} scope="col">{rotulo}</th>)}
                <th scope="col" className="actions-heading">Ações</th>
              </tr>
            </thead>
            <tbody>
              {registros.map((registro) => {
                const nome = colunas[0].valor(registro);
                return (
                  <tr key={registro.id}>
                    {colunas.map(({ rotulo, valor }) => (
                      <td key={rotulo}>{valor(registro) ?? '—'}</td>
                    ))}
                    <td className="actions-cell">
                      <Link to={`${rota}/editar/${registro.id}`} aria-label={`Editar ${nome}`}>
                        Editar
                      </Link>
                      <button
                        type="button"
                        className="delete-button"
                        disabled={excluindo !== null}
                        aria-label={`Excluir ${nome}`}
                        onClick={() => excluir(registro.id, nome)}
                      >
                        {excluindo === registro.id ? 'Excluindo…' : 'Excluir'}
                      </button>
                    </td>
                  </tr>
                );
              })}
              {registros.length === 0 && (
                <tr><td className="empty-state" colSpan={colunas.length + 1}>
                  Ainda não há registros.
                </td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
