package org.rmt2.api.handlers.maint;

import java.util.ArrayList;
import java.util.List;

import org.dto.ArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.util.media.ArtistTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the artist related message
 * handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class ArtistJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ArtistDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link ArtistDto}
     */
    public static final ArtistDto createArtistDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ArtistDto dto = Rmt2MediaDtoFactory.getAvArtistInstance(null);
        if (jaxbObj.getArtistId() != null) {
            dto.setId(jaxbObj.getArtistId());
        }
        dto.setName(jaxbObj.getArtistName());
        return dto;
    }

    /**
     * Creates an instance of <i>ArtistDto</i> using a valid <i>ArtistType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ArtistType}
     * @return an instance of {@link ArtistDto}
     */
    public static final ArtistDto createArtistDtoInstance(ArtistType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ArtistDto dto = Rmt2MediaDtoFactory.getAvArtistInstance(null);
        if (jaxbObj.getArtistId() != null) {
            dto.setId(jaxbObj.getArtistId());
        }
        dto.setName(jaxbObj.getArtistName());
        return dto;
    }

    /**
     * Creates an instance of <i>ArtistType</i> using a valid <i>ArtistDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link ArtistDto}
     * @return an instance of {@link ArtistType}
     */
    public static final ArtistType createArtistJaxbInstance(ArtistDto dto) {
        if (dto == null) {
            return null;
        }
        ArtistType obj = ArtistTypeBuilder.Builder.create()
                .withArtistName(dto.getName())
                .withArtistId(dto.getId())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>ArtistType</i> using a valid List of
     * <i>ArtistDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link ArtistDto}
     * @return List of {@link ArtistType} objects
     */
    public static final List<ArtistType> createArtistJaxbInstance(List<ArtistDto> results) {
        List<ArtistType> list = new ArrayList<>();
        for (ArtistDto item : results) {
            list.add(ArtistJaxbDtoFactory.createArtistJaxbInstance(item));
        }
        return list;
    }
}
