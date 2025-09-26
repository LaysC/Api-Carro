package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchFichaMarcaResponse {
    public List<FichaMarca> FichasMarca = new ArrayList<>();
    public long TotalFichasMarca;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
