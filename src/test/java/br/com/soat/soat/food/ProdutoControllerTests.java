package br.com.soat.soat.food;

import br.com.soat.soat.food.controller.ProdutoController;
import br.com.soat.soat.food.enums.Categoria;
import br.com.soat.soat.food.model.Cliente;
import br.com.soat.soat.food.model.Produto;
import br.com.soat.soat.food.services.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarOuAtualizarProduto() throws Exception {
        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição do Produto");
        produto.setPreco(BigDecimal.valueOf(50.0));

        Mockito.when(produtoService.cadastroEupdateProduto(any(Produto.class)))
                .thenReturn(produto);

        mockMvc.perform(post("/produto/cadastroProduto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Produto Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição do Produto"))
                .andExpect(jsonPath("$.preco").value(50.0));
    }

    @Test
    void deveListarProdutos() throws Exception {
        Produto produto1 = new Produto();
        produto1.setNome("Produto 1");

        Produto produto2 = new Produto();
        produto2.setNome("Produto 2");

        List<Produto> produtos = Arrays.asList(produto1, produto2);

        Mockito.when(produtoService.listarProdutos())
                .thenReturn(produtos);

        mockMvc.perform(get("/produto/listarProdutos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Produto 1"))
                .andExpect(jsonPath("$[1].nome").value("Produto 2"));
    }

    @Test
    void deveListarProdutosPorCategoria() throws Exception {
        Categoria categoria = Categoria.BEBIDA;

        Produto produto = new Produto();
        produto.setNome("Coca-Cola");
        produto.setCategoria(categoria);

        Mockito.when(produtoService.listarProdutosPorCategoria(eq(categoria)))
                .thenReturn(List.of(produto));

        mockMvc.perform(post("/produto/listarProdutosPorCategoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Coca-Cola"));
    }

    @Test
    void deveListarProdutosComDesconto() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Produto produto = new Produto();
        produto.setNome("Produto Desconto");
        produto.setPreco(BigDecimal.valueOf(40.0));

        Mockito.when(produtoService.setarDecontoProdutos(any(Cliente.class)))
                .thenReturn(List.of(produto));

        mockMvc.perform(post("/produto/listarProdutosDesconto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Produto Desconto"))
                .andExpect(jsonPath("$[0].preco").value(40.0));
    }

    @Test
    void deveDeletarProduto() throws Exception {
        Mockito.doNothing().when(produtoService).deletarProduto(1L);

        mockMvc.perform(delete("/produto/deletarProduto/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

