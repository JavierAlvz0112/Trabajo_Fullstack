package com.foodtruck.pedidos_service.repository;

import com.foodtruck.pedidos_service.model.Pedidos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedidos, Long> {

	List<Pedidos> findByFechaBetween(LocalDateTime from, LocalDateTime to);

	@Query("SELECT COALESCE(SUM(p.total),0) FROM Pedido p WHERE p.idUsuario = :idUsuario")
	Double sumTotalByIdUsuario(@Param("idUsuario") Long idUsuario);

	List<Pedidos> findByIdUsuario(Long idUsuario);

	List<Pedidos> findByDetalles_IdProducto(Long idProducto);
}
