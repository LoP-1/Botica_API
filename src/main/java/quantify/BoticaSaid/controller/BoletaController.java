package quantify.BoticaSaid.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.BoletaResponseDTO;
import quantify.BoticaSaid.dto.DetalleProductoDTO;
import quantify.BoticaSaid.model.Boleta;
import quantify.BoticaSaid.model.DetalleBoleta;
import quantify.BoticaSaid.repository.BoletaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boletas")
public class BoletaController {

    private final BoletaRepository boletaRepository;

    public BoletaController(BoletaRepository boletaRepository) {
        this.boletaRepository = boletaRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarBoletas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Specification<Boleta> spec = null;

        if (search != null && !search.isEmpty()) {
            Specification<Boleta> filtro = (root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("nombreCliente")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("id").as(String.class)), "%" + search.toLowerCase() + "%")
                    );
            spec = (spec == null) ? filtro : spec.and(filtro);
        }
        if (from != null && !from.isEmpty()) {
            Specification<Boleta> filtro = (root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("fechaVenta"), java.sql.Date.valueOf(from));
            spec = (spec == null) ? filtro : spec.and(filtro);
        }
        if (to != null && !to.isEmpty()) {
            Specification<Boleta> filtro = (root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("fechaVenta"), java.sql.Date.valueOf(to));
            spec = (spec == null) ? filtro : spec.and(filtro);
        }

        Page<Boleta> boletaPage = boletaRepository.findAll(spec, pageable);

        List<BoletaResponseDTO> boletas = boletaPage.getContent().stream().map(boleta -> {
            BoletaResponseDTO dto = new BoletaResponseDTO();
            dto.setId(boleta.getId() != null ? boleta.getId().longValue() : null);
            dto.setNumero(boleta.getId() != null ? boleta.getId().toString() : null);
            dto.setFecha(boleta.getFechaVenta() != null ? boleta.getFechaVenta().toString() : "");
            dto.setCliente(boleta.getNombreCliente());
            dto.setTotalCompra(boleta.getTotalCompra());
            dto.setVuelto(boleta.getVuelto());
            dto.setMetodoPago(
                    (boleta.getMetodoPago() != null && boleta.getMetodoPago().getNombre() != null)
                            ? boleta.getMetodoPago().getNombre().toString()
                            : ""
            );
            dto.setUsuario(
                    boleta.getUsuario() != null ? boleta.getUsuario().getNombreCompleto() : ""
            );
            List<DetalleProductoDTO> productosVendidos = boleta.getDetalles() != null
                    ? boleta.getDetalles().stream().map((DetalleBoleta detalle) -> {
                DetalleProductoDTO prodDto = new DetalleProductoDTO();
                if (detalle.getProducto() != null) {
                    prodDto.setCodBarras(detalle.getProducto().getCodigoBarras());
                    prodDto.setNombre(detalle.getProducto().getNombre());
                }
                prodDto.setCantidad(detalle.getCantidad());
                prodDto.setPrecio(detalle.getPrecioUnitario());
                return prodDto;
            }).collect(Collectors.toList())
                    : List.of();
            dto.setProductos(productosVendidos);

            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("boletas", boletas);
        response.put("total", boletaPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
}