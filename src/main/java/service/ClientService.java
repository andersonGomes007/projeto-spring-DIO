package service;


import model.Client;

public interface ClientService {

	Iterable<Client> buscarTodos();

	Client buscarPorId(Long id);

	void inserir(Client cliente);

	void atualizar(Long id, Client cliente);

	void deletar(Long id);

}
