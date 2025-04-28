package impl;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Client;
import model.ClientRepository;
import model.Endereco;
import model.EnderecoRepository;
import service.ClientService;
import service.ViaCepService;

@Service
public class ClientServiceImpl implements ClientService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClientRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Client> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Client buscarPorId(Long id) {
		// Buscar Cliente por ID.
		Optional<Client> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Client cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Client cliente) {
		// Buscar Cliente por ID, caso exista:
		Optional<Client> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID.
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Client cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

}
