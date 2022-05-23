package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class ClienteServiceImpl implements ClienteService {

    // SINGLETON: Injetar os componentes do Spring com o @Autowired.

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;

    // STRATEGY: Implementar os métodos definidos na interface.
    // FACADE: Abstrair integrações com subsistemas, provendo uma interface simples.

    @Override
    public Iterable<Cliente> buscarTodos() {
        // Buscar todos os clientes.
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        // Buscar clientes por ID.
        Optional<Cliente> cliente = clienteRepository.findById(String.valueOf(id));
        return cliente.get();
    }

    @Override
    public void inserir (Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    private void salvarClienteComCep(Cliente cliente) {
        // Verificar se o endereço do cliente já existe (pelo CEP).
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {

            // Caso não exista, integrar com o ViaCep e persistir o retorno.
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);

        // Inserir cliente, vinculando endereço (novo ou existente).
        clienteRepository.save(cliente);
    }

    @Override
    public void atualizar (Long id, Cliente cliente) {
        // Buscar cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(String.valueOf(id));
        if (clienteBd.isPresent()) {
            // Verificar se o endereço do cliente já existe (pelo CEP).
            // Caso não exista, integrar com o ViaCep e persistir o retorno.
            // Alterar cliente, vinculando endereço (novo ou existente).
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar (Long id) {
        clienteRepository.deleteById(String.valueOf(id));
    }
}
