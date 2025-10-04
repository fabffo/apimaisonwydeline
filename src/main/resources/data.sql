-- ===== Produits =====
INSERT INTO produit (nom, description, prix, image_url, precommande, option_couleur) VALUES
 ('slingBackBordeaux',       'Slingback en cuir bordeaux.',              139.00, 'slingBackBordeaux/',       false, false),
 ('slingBackJeans',          'Slingback en denim jeans.',                129.00, 'slingBackJeans/',          false, false),
 ('slingBackCamels',         'Slingback cuir camel.',                    135.00, 'slingBackCamels/',         false, false),
 ('chaussuresPlatesBlanches','Chaussures plates blanches confort.',       99.00, 'chaussuresPlatesBlanches/',false, false),
 ('bottesNoiresNappa',       'Bottes en cuir Nappa noir.',               219.00, 'bottesNoiresNappa/',       true,  false),
 ('bottesVertesTruffes',     'Bottes vert truffe (édition limitée).',    229.00, 'bottesVertesTruffes/',     true,  false),
 ('bottinesDaimsNoires',     'Bottines en daim noir.',                   189.00, 'bottinesDaimsNoires/',     false, false),
 ('bottinesBordeaux',        'Bottines bordeaux élégantes.',             189.00, 'bottinesBordeaux/',        false, false),
 ('bottinesNoires',       	 'Bottines noires élégantes.',             	 189.00, 'bottinesBordeaux/',        false, false);
-- ===== Stock par tailles =====
INSERT INTO stock_taille (id, produit_id, taille, couleur, quantite) VALUES
 (1001, 1, '36', NULL, 5), (1002, 1, '39', NULL, 6),
 (1101, 2, '36', NULL, 4), (1102, 2, '37', NULL, 7),
 (1201, 3, '36', NULL, 3), (1202, 3, '39', NULL, 5),
 (1301, 4, '36', NULL,10), (1302, 4, '40', NULL, 8),
 (1401, 5, '36', NULL, 2), (1402, 5, '37', NULL, 2),
 (1501, 6, '36', NULL, 1), (1502, 6, '41', NULL, 1),
 (1601, 7, '36', NULL, 6), (1602, 7, '38', NULL, 6),
 (1701, 8, '36', NULL, 4), (1702, 8, '38', NULL, 4),
 (1801, 9, '36', NULL, 4), (1802, 9, '38', NULL, 4);

 -- Image plein écran
INSERT INTO image_deco (nom, image_url, description)
VALUES ('Look 01', '/images/collection/deco/look01.jpg','Hero');

-- Deux images pour slide GRID2
INSERT INTO image_deco (nom, image_url, description)
VALUES ('Look 02A','/images/collection/deco/look02a.jpg','Left'),
       ('Look 02B','/images/collection/deco/look02b.jpg','Right');
       INSERT INTO image_deco (nom, image_url, description)
VALUES ('Look 03', '/images/collection/deco/look03.jpg','FULL');
-- ========== SLIDES CORRIGÉS ==========

-- Page 1 : FULL imageDeco
INSERT INTO slide (id, nom_image, page_order, type_image, full_kind, image_full_id)
VALUES (1, 'Page 1', 1, 'FULL', 'DECO', 1);

-- Page 2 : GRID2 produit à gauche / imageDeco à droite
INSERT INTO slide (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, image_right_id)
VALUES (2, 'Page 2', 2, 'GRID2', 'PRODUIT', 1, 'DECO', 2);

-- Page 3 : FULL produit
INSERT INTO slide (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (3, 'Page 3', 3, 'FULL', 'PRODUIT', 2);

-- Page 4 : FULL imageDeco
INSERT INTO slide (id, nom_image, page_order, type_image, full_kind, image_full_id)
VALUES (4, 'Page 4', 4, 'FULL', 'DECO', 4);

-- Page 5 : GRID2 produit à gauche / produit à droite
INSERT INTO slide (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, product_right_id)
VALUES (5, 'Page 5', 5, 'GRID2', 'PRODUIT', 3, 'PRODUIT', 9);

-- Page 6 : GRID2 produit à gauche / produit à droite
INSERT INTO slide (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, product_right_id)
VALUES (6, 'Page 6', 6, 'GRID2', 'PRODUIT', 8, 'PRODUIT', 5);

-- Page 7 : GRID2 produit à gauche / produit à droite
INSERT INTO slide (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, product_right_id)
VALUES (7, 'Page 7', 7, 'GRID2', 'PRODUIT', 6, 'PRODUIT', 7);

-- Page 8 : FULL produit
INSERT INTO slide (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (8, 'Page 8', 8, 'FULL', 'PRODUIT', 4);



-- Page 1 : FULL imageDeco
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (1, 'Page 1', 1, 'FULL', 'PRODUIT', 1);

-- Page 2 : GRID2 produit à gauche / imageDeco à droite
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, product_right_id)
VALUES (2, 'Page 2', 2, 'GRID2', 'PRODUIT', 2, 'PRODUIT', 3);

-- Page 3 : FULL produit
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (3, 'Page 3', 3, 'FULL', 'PRODUIT', 4);

-- Page 4 : FULL imageDeco
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (4, 'Page 4', 4, 'FULL', 'PRODUIT', 5);

-- Page 5 : GRID2 produit à gauche / produit à droite
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, left_kind, product_left_id, right_kind, product_right_id)
VALUES (5, 'Page 5', 5, 'GRID2', 'PRODUIT', 6, 'PRODUIT', 7);

-- Page 6 : FULL produit
INSERT INTO slide_vitrine (id, nom_image, page_order, type_image, full_kind, product_full_id)
VALUES (6, 'Page 6', 6, 'FULL', 'PRODUIT', 8);

-- schema.sql (ou migration)
CREATE TABLE IF NOT EXISTS paiement_log (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  stripe_event_id VARCHAR(100) NOT NULL,
  event_type VARCHAR(100) NOT NULL,
  livemode BOOLEAN NOT NULL,
  order_id BIGINT,
  session_id VARCHAR(120),
  payment_intent_id VARCHAR(120),
  status VARCHAR(60),
  amount BIGINT,
  currency VARCHAR(10),
  customer_email VARCHAR(180),
  created_at_stripe TIMESTAMP WITH TIME ZONE,
  received_at_app TIMESTAMP WITH TIME ZONE NOT NULL,
  payload_json CLOB,
  processed_ok BOOLEAN,
  processed_message VARCHAR(500)
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_stripe_event_id ON paiement_log(stripe_event_id);
