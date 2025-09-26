package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchMarcaResponse {
    public List<Marca> Marcas = new ArrayList<>();
    public long TotalMarcas;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
