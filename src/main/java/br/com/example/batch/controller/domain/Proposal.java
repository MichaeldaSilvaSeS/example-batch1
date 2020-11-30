package br.com.example.batch.controller.domain;

import br.com.example.batch.controller.domain.Client;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Proposal {
    private Long id;

    private Client client;

}
