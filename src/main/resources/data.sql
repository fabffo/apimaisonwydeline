-- ===== Produits =====
INSERT INTO produit (nom, description, prix, image_url, precommande, option_couleur) VALUES
 ('slingBackBordeaux',       'Slingback en cuir bordeaux.',              139.00, 'slingBackBordeaux/',       false, false),
 ('slingBackJeans',          'Slingback en denim jeans.',                129.00, 'slingBackJeans/',          false, false),
 ('slingBackCamels',         'Slingback cuir camel.',                    135.00, 'slingBackCamels/',         false, false),
 ('chaussuresPlatesBlanches','Chaussures plates blanches confort.',       99.00, 'chaussuresPlatesBlanches/',false, false),
 ('bottesNoiresNappa',       'Bottes en cuir Nappa noir.',               219.00, 'bottesNoiresNappa/',       true,  false),
 ('bottesVertesTruffes',     'Bottes vert truffe (édition limitée).',    229.00, 'bottesVertesTruffes/',     true,  false),
 ('bottinesDaimsNoires',     'Bottines en daim noir.',                   189.00, 'bottinesDaimsNoires/',     false, false),
 ('bottinesBordeaux',        'Bottines bordeaux élégantes.',             189.00, 'bottinesBordeaux/',        false, false);

-- ===== Stock par tailles =====
INSERT INTO stock_taille (id, produit_id, taille, couleur, quantite) VALUES
 (1001, 1, '36', NULL, 5), (1002, 1, '39', NULL, 6),
 (1101, 2, '36', NULL, 4), (1102, 2, '37', NULL, 7),
 (1201, 3, '36', NULL, 3), (1202, 3, '39', NULL, 5),
 (1301, 4, '36', NULL,10), (1302, 4, '40', NULL, 8),
 (1401, 5, '36', NULL, 2), (1402, 5, '37', NULL, 2),
 (1501, 6, '36', NULL, 1), (1502, 6, '41', NULL, 1),
 (1601, 7, '36', NULL, 6), (1602, 7, '38', NULL, 6),
 (1701, 8, '36', NULL, 4), (1702, 8, '38', NULL, 4);
