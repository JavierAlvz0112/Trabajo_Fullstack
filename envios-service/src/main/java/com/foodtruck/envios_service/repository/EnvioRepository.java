package com.foodtruck.envios_service.repository;
import com.foodtruck.envios_service.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByIdPedido(Long idPedido);
    List<Envio> findByEstado(String estado);
}