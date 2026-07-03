package com.foodtruck.pagos_service.repository;

import com.foodtruck.pagos_service.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

	List<Pago> findByIdPedido(Long idPedido);

	List<Pago> findByEstado(String estado);

	List<Pago> findByFechaPagoBetween(java.util.Date from, java.util.Date to);

	@Query("SELECT COALESCE(SUM(p.monto),0) FROM Pago p WHERE p.idPedido = :pedidoId")
	Double sumMontoByPedido(@Param("pedidoId") Long pedidoId);
}
