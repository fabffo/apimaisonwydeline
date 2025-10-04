package com.wydeline.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wydeline.dto.ResumeProduit;
import com.wydeline.dto.SlideVitrineVM;
import com.wydeline.model.ImageDeco;
import com.wydeline.model.Produit;
import com.wydeline.model.SlideVitrine;
import com.wydeline.repository.SlideVitrineRepository;

@Service
public class VitrineService {

    private final SlideVitrineRepository slideVitrineRepo;
    private final ProduitService produitService;

    public VitrineService(SlideVitrineRepository slideVitrineRepo, ProduitService produitService) {
        this.slideVitrineRepo = slideVitrineRepo;
        this.produitService = produitService;
    }

    public List<SlideVitrineVM> buildVitrineVM() {
        List<SlideVitrine> slideVitrines = slideVitrineRepo.findAllByOrderByPageOrderAsc();
        List<ResumeProduit> fallback = Optional.ofNullable(produitService.findTop8Disponibles())
                                               .orElseGet(List::of);
        int[] idx = {0}; // index fallback

        List<SlideVitrineVM> out = new ArrayList<>();
        for (SlideVitrine s : slideVitrines) {
            SlideVitrineVM vm = new SlideVitrineVM();
            vm.pageOrder = s.getPageOrder();
            vm.type = (s.getTypeImage() == SlideVitrine.TypeImage.FULL) ? SlideVitrineVM.Type.FULL : SlideVitrineVM.Type.GRID2;
            vm.images = new ArrayList<>();

            if (s.getTypeImage() == SlideVitrine.TypeImage.FULL) {
                vm.images.add(buildImageVM(s.getFullKind(), s.getImageFull(), s.getProductFull(), fallback, idx));
            } else {
                vm.images.add(buildImageVM(s.getLeftKind(),  s.getImageLeft(),  s.getProductLeft(),  fallback, idx));
                vm.images.add(buildImageVM(s.getRightKind(), s.getImageRight(), s.getProductRight(), fallback, idx));
            }

            out.add(vm);
        }
        out.sort(Comparator.comparingInt(a -> a.pageOrder));
        return out;
    }

    private SlideVitrineVM.SlideVitrineImageVM buildImageVM(SlideVitrine.ImageKind kind,
                                              ImageDeco deco,
                                              Produit produitEntity,
                                              List<ResumeProduit> fallback,
                                              int[] idx) {
        SlideVitrineVM.SlideVitrineImageVM v = new SlideVitrineVM.SlideVitrineImageVM();

        if (kind == SlideVitrine.ImageKind.PRODUIT) {
            v.imageType = SlideVitrineVM.ImageType.PRODUIT;

            // Préférence : produit référencé dans le slideVitrine
            ResumeProduit prod = null;
            if (produitEntity != null) {
                prod = produitService.resumeFromProduit(produitEntity);
            } else if (idx[0] < fallback.size()) {
                prod = fallback.get(idx[0]++);
            }

            if (prod != null) {
                v.produitId  = prod.getId();
                v.produitNom = prod.getNom();
                v.prix       = prod.getPrix();
                v.tailles    = prod.getTailles();
                // image d’illustration : si tu veux afficher la photo produit, mets-la ici
                v.src = (deco != null && deco.getImageUrl() != null) ? deco.getImageUrl() : imageProduitFallback(prod);
                v.alt = (deco != null && deco.getNom() != null) ? deco.getNom() : prod.getNom();
            } else {
                // Pas de produit dispo → afficher une image vide ou déco
                v.src = (deco != null) ? deco.getImageUrl() : "";
                v.alt = (deco != null) ? Optional.ofNullable(deco.getNom()).orElse(deco.getDescription()) : "Produit indisponible";
            }

        } else { // DECO
            v.imageType = SlideVitrineVM.ImageType.DECO;
            if (deco != null) {
                v.src = deco.getImageUrl();
                v.alt = (deco.getNom() != null ? deco.getNom() : deco.getDescription());
            } else {
                v.src = "";
                v.alt = "Image indisponible";
            }
        }

        return v;
    }

    /** Si tu veux une image produit par défaut quand aucune ImageDeco n’est fournie pour PRODUIT. */
    private String imageProduitFallback(ResumeProduit prod) {
        // TODO : retourner l’URL d’image produit si tu l’as (sinon, un placeholder)
        return "/images/vitrine/produit/" + prod.getNom() + ".jpg";
    }
}
