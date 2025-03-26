package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "Roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        // Если объект является прокси-объектом
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer ().getPersistentClass ()
                : o.getClass ();

        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer ().getPersistentClass ()
                : this.getClass ();

        // Если классы разные, считаем объекты разными
        if (thisEffectiveClass != oEffectiveClass) return false;

        Role role = (Role) o;

        return id == role.id;
    }

    @Override
    public final int hashCode() {
        // Используем getClass().hashCode() для обычных объектов, для прокси - берем класс через LazyInitializer
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer ().getPersistentClass ().hashCode ()
                : getClass ().hashCode ();
    }
}
