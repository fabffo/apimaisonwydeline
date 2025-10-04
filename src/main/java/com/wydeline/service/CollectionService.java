package com.wydeline.service;

import com.wydeline.dto.ResumeProduit;
import com.wydeline.dto.SlideVM;
import com.wydeline.model.ImageDeco;
import com.wydeline.model.Produit;
import com.wydeline.model.Slide;
import com.wydeline.repository.SlideRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollectionService {

    private final SlideRepository slideRepo;
    private final ProduitService produitService;

    public CollectionService(SlideRepository slideRepo, ProduitService produitService) {
        this.slideRepo = slideRepo;
        this.produitService = produitService;
    }

    public List<SlideVM> buildCollectionVM() {
        List<Slide> slides = slideRepo.findAllByOrderByPageOrderAsc();
        List<ResumeProduit> fallback = Optional.ofNullable(produitService.findTop8Disponibles())
                                               .orElseGet(List::of);
        int[] idx = {0}; // index fallback

        List<SlideVM> out = new ArrayList<>();
        for (Slide s : slides) {
            SlideVM vm = new SlideVM();
            vm.pageOrder = s.getPageOrder();
            vm.type = (s.getTypeImage() == Slide.TypeImage.FULL) ? SlideVM.Type.FULL : SlideVM.Type.GRID2;
            vm.images = new ArrayList<>();

            if (s.getTypeImage() == Slide.TypeImage.FULL) {
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

    private SlideVM.SlideImageVM buildImageVM(Slide.ImageKind kind,
                                              ImageDeco deco,
                                              Produit produitEntity,
                                              List<ResumeProduit> fallback,
                                              int[] idx) {
        SlideVM.SlideImageVM v = new SlideVM.SlideImageVM();

        if (kind == Slide.ImageKind.PRODUIT) {
            v.imageType = SlideVM.ImageType.PRODUIT;

            // Préférence : produit référencé dans le slide
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
            v.imageType = SlideVM.ImageType.DECO;
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
        return "/images/collection/produit/" + prod.getNom() + ".jpg";
    }
}
