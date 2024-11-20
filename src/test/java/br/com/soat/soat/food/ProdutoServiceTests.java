package br.com.soat.soat.food;

import br.com.soat.soat.food.enums.Categoria;
import br.com.soat.soat.food.model.Produto;
import br.com.soat.soat.food.model.Cliente;
import br.com.soat.soat.food.repository.ProdutoRepository;
import br.com.soat.soat.food.services.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTests {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarOuAtualizarProduto() {
        Produto produto = new Produto();
        produto.setNome("Pizza");
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto resultado = produtoService.cadastroEupdateProduto(produto);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveListarProdutosOrdenadosPorCategoria() {
        Produto p1 = new Produto();
        p1.setCategoria(Categoria.BEBIDA);
        Produto p2 = new Produto();
        p2.setCategoria(Categoria.LANCHE);

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(p2, p1));

        List<Produto> produtos = produtoService.listarProdutos();

        assertEquals(2, produtos.size());
        assertEquals(Categoria.BEBIDA, produtos.get(0).getCategoria());
    }

    @Test
    void deveAplicarDescontoAosProdutos() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Produto p1 = new Produto();
        p1.setPreco(new BigDecimal("50"));

        Produto p2 = new Produto();
        p2.setPreco(new BigDecimal("5"));

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Produto> produtosComDesconto = produtoService.setarDecontoProdutos(cliente);

        assertEquals(2, produtosComDesconto.size());
        assertEquals(new BigDecimal("40"), produtosComDesconto.get(0).getPreco());
        assertEquals(new BigDecimal("5"), produtosComDesconto.get(1).getPreco());
    }

    @Test
    void deveListarProdutosSemDescontoParaClienteSemId() {
        Cliente cliente = new Cliente();

        Produto p1 = new Produto();
        p1.setPreco(new BigDecimal("50"));

        Produto p2 = new Produto();
        p2.setPreco(new BigDecimal("5"));

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Produto> produtos = produtoService.setarDecontoProdutos(cliente);

        assertEquals(2, produtos.size());
        assertEquals(new BigDecimal("50"), produtos.get(0).getPreco());
    }

    @Test
    void deveDeletarProdutoPorId() {
        Long id = 1L;
        Produto produto = new Produto();
        produto.setNome("Pizza");

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).deleteById(id);

        produtoService.deletarProduto(id);

        verify(produtoRepository, times(1)).findById(id);
        verify(produtoRepository, times(1)).deleteById(id);
    }

    @Test
    void naoDeveDeletarProdutoSeNaoExistir() {
        Long id = 1L;

        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        produtoService.deletarProduto(id);

        verify(produtoRepository, times(1)).findById(id);
        verify(produtoRepository, never()).deleteById(any());
    }

    @Test
    void deveListarProdutosPorCategoria() {
        Categoria categoria = Categoria.LANCHE;
        Produto p1 = new Produto();
        p1.setCategoria(categoria);

        when(produtoRepository.findByCategoria(categoria)).thenReturn(List.of(p1));

        List<Produto> produtos = produtoService.listarProdutosPorCategoria(categoria);

        assertEquals(1, produtos.size());
        assertEquals(Categoria.LANCHE, produtos.get(0).getCategoria());
    }
}