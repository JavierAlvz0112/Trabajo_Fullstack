INSERT INTO pedido (id_usuario, total, estado, fecha) VALUES
(1, 4300.0, 'pendiente', '2026-07-01'),
(2, 1800.0, 'entregado', '2026-07-02'),
(1, 3600.0, 'pagado',    '2026-07-03');

INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, subtotal) VALUES
(1, 1, 2, 2500.0),
(1, 2, 1, 1800.0),
(2, 3, 1, 1800.0),
(3, 1, 1, 1500.0),
(3, 2, 2, 2100.0);