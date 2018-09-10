package com.lucas.medidordeimpacto;

public class Book {
    private int id;
    private String position;
    private float mean_acceleration;

    public Book(){}
    public Book(String pos_read, float input_mean_accel) {
        super();
        this.position = pos_read;
        this.mean_acceleration = input_mean_acel;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String get_position()
    {
        return position;
    }

    public String get_mean_acceleration()
    {
        return mean_acceleration;
    }

    public void set_position(String input_pos)
    {
        this.position = input_pos;
    }

    public void set_mean_acceleration(float input_mean_accel)
    {
        this.mean_acceleration = input_mean_accel;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + position + ", mean_accel=" + Float.toString(input_mean_accel) + "]";
    }
}
