package com.lucas.medidordeimpacto;

public class Book {
    private int id;
    private String kilometragem;
    private String aceleracao;

    public Book(){}
    public Book(String km_read, String a_read) {
        super();
        this.kilometragem = km_read;
        this.aceleracao = a_read;
    }
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String get_modeloCarro()
    {
        return kilometragem;
    }

    public void set_modeloCarro(String title)
    {
        this.kilometragem = title;
    }

    public String get_valorCarro()
    {
        return aceleracao;
    }

    public void set_valorCarro(String author)
    {
        this.aceleracao = author;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + kilometragem + ", author=" + aceleracao + "]";
    }
}
