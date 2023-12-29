package com.example.poc;

public class UserAPI {
    private String _id;
    private String nome;
    private String email;
    private String password;

    public UserAPI(String _id, String nome, String email, String password) {
        this._id = _id;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return nome;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
