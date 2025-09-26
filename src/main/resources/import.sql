-- Insere dados básicos de teste para garantir a integridade do banco

-- FichaMarca
insert into FichaMarca (historia, fundadores, premiosConquistados) values ('História da Ford.', 'Henry Ford', 'Carro do Ano da América do Norte');
insert into FichaMarca (historia, fundadores, premiosConquistados) values ('História da Tesla.', 'Elon Musk', 'Inovação da TIME Magazine');

-- Marca
insert into Marca (nomeDaMarca, paisDeOrigem, ficha_marca_id) values ('Ford', 'Estados Unidos', 1);
insert into Marca (nomeDaMarca, paisDeOrigem, ficha_marca_id) values ('Tesla', 'Estados Unidos', 2);

-- Acessorio
insert into Acessorio (nome, descricao) values ('Ar Condicionado', 'Sistema de resfriamento.');
insert into Acessorio (nome, descricao) values ('GPS', 'Navegação por satélite.');
insert into Acessorio (nome, descricao) values ('Teto Solar', 'Vidro panorâmico.');

-- Carro
insert into Carro (modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values ('Mustang', 2023, 1, 'Carro esportivo lendário', 9.5, 5000);
insert into Carro (modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values ('Fusion', 2020, 1, 'Sedan confortável e seguro.', 8.5, 2500);
insert into Carro (modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values ('Model S', 2023, 2, 'Carro elétrico de luxo.', 9.9, 0);

-- Relacionamento carro_acessorio
insert into carro_acessorio (carro_id, acessorio_id) values(1, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(1, 2);
insert into carro_acessorio (carro_id, acessorio_id) values(2, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(3, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(3, 3);
