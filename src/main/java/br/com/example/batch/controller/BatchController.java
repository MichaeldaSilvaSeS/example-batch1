package br.com.example.batch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//https://atitudereflexiva.wordpress.com/2020/03/02/spring-batch-introducao/
@RestController
public class BatchController {

    @Autowired
    private BatchExecutor executor;

    @GetMapping(path = "/batches")
    public String executeBatch() {
        String retorno = "";
        try {
            executor.execute();
            retorno = "Sucesso !!!";
        }catch (Exception e){
            retorno = "Falha !!!";
        }
        return retorno;
    }
}
