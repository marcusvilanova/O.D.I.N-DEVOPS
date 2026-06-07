package br.com.odin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class OdinDevopsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdinDevopsApiApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS missoes (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(120) NOT NULL,
                    objetivo VARCHAR(255) NOT NULL,
                    status VARCHAR(50) NOT NULL
                )
            """);

            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS alertas (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    missao_id BIGINT NOT NULL,
                    descricao VARCHAR(255) NOT NULL,
                    severidade VARCHAR(50) NOT NULL,
                    CONSTRAINT fk_alerta_missao
                        FOREIGN KEY (missao_id)
                        REFERENCES missoes(id)
                        ON DELETE CASCADE
                )
            """);
        };
    }
}

@RestController
@RequestMapping("/api/missoes")
class MissaoController {

    private final JdbcTemplate jdbcTemplate;

    MissaoController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return jdbcTemplate.queryForList("SELECT * FROM missoes ORDER BY id");
    }

    @GetMapping("/{id}")
    public Map<String, Object> buscarPorId(@PathVariable Long id) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                "SELECT * FROM missoes WHERE id = ?", id
        );

        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada");
        }

        return resultado.get(0);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> criar(@RequestBody Map<String, String> body) {
        jdbcTemplate.update(
                "INSERT INTO missoes (nome, objetivo, status) VALUES (?, ?, ?)",
                body.get("nome"),
                body.get("objetivo"),
                body.get("status")
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        int linhas = jdbcTemplate.update(
                "UPDATE missoes SET nome = ?, objetivo = ?, status = ? WHERE id = ?",
                body.get("nome"),
                body.get("objetivo"),
                body.get("status"),
                id
        );

        if (linhas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada");
        }

        return buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        int linhas = jdbcTemplate.update("DELETE FROM missoes WHERE id = ?", id);

        if (linhas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada");
        }
    }
}

@RestController
@RequestMapping("/api/alertas")
class AlertaController {

    private final JdbcTemplate jdbcTemplate;

    AlertaController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return jdbcTemplate.queryForList("""
            SELECT
                a.id,
                a.missao_id,
                m.nome AS nome_missao,
                a.descricao,
                a.severidade
            FROM alertas a
            INNER JOIN missoes m ON m.id = a.missao_id
            ORDER BY a.id
        """);
    }

    @GetMapping("/{id}")
    public Map<String, Object> buscarPorId(@PathVariable Long id) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList("""
            SELECT
                a.id,
                a.missao_id,
                m.nome AS nome_missao,
                a.descricao,
                a.severidade
            FROM alertas a
            INNER JOIN missoes m ON m.id = a.missao_id
            WHERE a.id = ?
        """, id);

        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta nao encontrado");
        }

        return resultado.get(0);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> criar(@RequestBody Map<String, String> body) {
        jdbcTemplate.update(
                "INSERT INTO alertas (missao_id, descricao, severidade) VALUES (?, ?, ?)",
                Long.valueOf(body.get("missao_id")),
                body.get("descricao"),
                body.get("severidade")
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        int linhas = jdbcTemplate.update(
                "UPDATE alertas SET missao_id = ?, descricao = ?, severidade = ? WHERE id = ?",
                Long.valueOf(body.get("missao_id")),
                body.get("descricao"),
                body.get("severidade"),
                id
        );

        if (linhas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta nao encontrado");
        }

        return buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        int linhas = jdbcTemplate.update("DELETE FROM alertas WHERE id = ?", id);

        if (linhas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta nao encontrado");
        }
    }
}
