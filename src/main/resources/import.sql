-- Insere dados básicos de teste para garantir a integridade do banco

-- FichaMarca
insert into FichaMarca (id, historia, fundadores, premiosConquistados) values (1, 'História da Ford.', 'Henry Ford', 'Carro do Ano da América do Norte');
insert into FichaMarca (id, historia, fundadores, premiosConquistados) values (2, 'História da Tesla.', 'Elon Musk', 'Inovação da TIME Magazine');

-- Marca
insert into Marca (id, nomeDaMarca, paisDeOrigem, ficha_marca_id) values (1, 'Ford', 'Estados Unidos', 1);
insert into Marca (id, nomeDaMarca, paisDeOrigem, ficha_marca_id) values (2, 'Tesla', 'Estados Unidos', 2);

-- Acessorio
insert into Acessorio (id, nome, descricao) values (1, 'Ar Condicionado', 'Sistema de resfriamento.');
insert into Acessorio (id, nome, descricao) values (2, 'GPS', 'Navegação por satélite.');
insert into Acessorio (id, nome, descricao) values (3, 'Teto Solar', 'Vidro panorâmico.');

-- Carro
insert into Carro (id, modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values (1, 'Mustang', 2023, 1, 'Carro esportivo lendário', 9.5, 5000);
insert into Carro (id, modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values (2, 'Fusion', 2020, 1, 'Sedan confortável e seguro.', 8.5, 2500);
insert into Carro (id, modelo, anoFabricacao, marca_id, descricao, avaliacao, cilindradas) values (3, 'Model S', 2023, 2, 'Carro elétrico de luxo.', 9.9, 0);

-- Relacionamento carro_acessorio
insert into carro_acessorio (carro_id, acessorio_id) values(1, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(1, 2);
insert into carro_acessorio (carro_id, acessorio_id) values(2, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(3, 1);
insert into carro_acessorio (carro_id, acessorio_id) values(3, 3);
