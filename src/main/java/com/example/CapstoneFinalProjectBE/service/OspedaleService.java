package com.example.CapstoneFinalProjectBE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Evento;
import com.example.CapstoneFinalProjectBE.model.Ospedale;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.repository.OspedaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class OspedaleService {

    @Autowired
    private OspedaleRepository ospedaleRepo;

    @Autowired
    private Cloudinary cloudinary;

    //  CREAZIONE OSPEDALE (Solo Admin) con upload immagine su Cloudinary
    @PreAuthorize("hasRole('ADMIN')")
    public String creaOspedale(OspedaleDTO dto, MultipartFile imgOspedale) throws IOException {
        Ospedale ospedale = dtoToEntity(dto);

        //  Se l'Admin carica un'immagine, la carichiamo su Cloudinary
        if (imgOspedale != null && !imgOspedale.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgOspedale.getBytes(), ObjectUtils.emptyMap());
            ospedale.setImgOspedale((String) uploadResult.get("secure_url"));
        }

        ospedaleRepo.save(ospedale);
        return "Ospedale creato con ID: " + ospedale.getId();
    }

    // OTTIENI UN OSPEDALE PER ID (Accesso libero)
    public OspedaleDTO getOspedaleById(Long id) {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        return entityToDto(ospedale);
    }

    //  OTTIENI TUTTI GLI OSPEDALI (Accesso libero)
    public Page<OspedaleDTO> getAllOspedali(Pageable pageable) {
        Page<Ospedale> listaOspedali = ospedaleRepo.findAll(pageable);
        List<OspedaleDTO> listaOspedaliDTO = new ArrayList<>();

        for (Ospedale ospedale : listaOspedali.getContent()) {
            listaOspedaliDTO.add(entityToDto(ospedale));
        }

        return new PageImpl<>(listaOspedaliDTO, pageable, listaOspedali.getTotalElements());
    }

    // MODIFICA GENERALE OSPEDALE (Solo Admin) - Possibilità di aggiornare immagine
    @PreAuthorize("hasRole('ADMIN')")
    public String modificaOspedale(Long id, OspedaleDTO dto, MultipartFile imgFile) throws IOException {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        ospedale.setNome(dto.getNome());
        ospedale.setIndirizzo(dto.getIndirizzo());
        ospedale.setEmail(dto.getEmail());

        //  Se l'Admin carica una nuova immagine, la aggiorniamo
        if (imgFile != null && !imgFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgFile.getBytes(), ObjectUtils.emptyMap());
            ospedale.setImgOspedale((String) uploadResult.get("secure_url"));
        }

        ospedaleRepo.save(ospedale);
        return "Ospedale con ID: " + id + " modificato con successo.";
    }

    //  ELIMINA OSPEDALE (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOspedale(Long id) {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        ospedaleRepo.delete(ospedale);
        return "Ospedale con ID: " + id + " eliminato con successo.";
    }

    //  TRAVASO DTO → ENTITY
    private Ospedale dtoToEntity(OspedaleDTO dto) {
        Ospedale ospedale = new Ospedale();
        ospedale.setNome(dto.getNome());
        ospedale.setIndirizzo(dto.getIndirizzo());
        ospedale.setEmail(dto.getEmail());
        ospedale.setImgOspedale(dto.getImgOspedale());
        return ospedale;
    }

    //  TRAVASO ENTITY → DTO (con lista di eventi SENZA riferimenti all'ospedale per evitare loop)
    private OspedaleDTO entityToDto(Ospedale ospedale) {
        OspedaleDTO dto = new OspedaleDTO();
        dto.setId(ospedale.getId());
        dto.setNome(ospedale.getNome());
        dto.setIndirizzo(ospedale.getIndirizzo());
        dto.setEmail(ospedale.getEmail());
        dto.setImgOspedale(ospedale.getImgOspedale());

        //  Passiamo la lista degli eventi SENZA l'ospedale per evitare ricorsione infinita
        List<EventoDTO> eventiDTO = new ArrayList<>();
        if (ospedale.getEventi() != null) {
            for (Evento evento : ospedale.getEventi()) {
                EventoDTO eventoDTO = new EventoDTO();
                eventoDTO.setId(evento.getId());
                eventoDTO.setTitolo(evento.getTitolo());
                eventoDTO.setDescrizione(evento.getDescrizione());
                eventoDTO.setData(evento.getData());
                eventoDTO.setImgEvento(evento.getImgEvento());
                eventiDTO.add(eventoDTO);
            }
        }
        dto.setEventi(eventiDTO);

        return dto;
    }
}
