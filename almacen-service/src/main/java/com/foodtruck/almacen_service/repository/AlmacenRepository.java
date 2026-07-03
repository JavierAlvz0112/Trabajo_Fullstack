package com.foodtruck.almacen_service.repository;

import com.foodtruck.almacen_service.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    List<Almacen> findByIdProducto(Long idProducto);
    Optional<Almacen> findFirstByIdProducto(Long idProducto);
}
