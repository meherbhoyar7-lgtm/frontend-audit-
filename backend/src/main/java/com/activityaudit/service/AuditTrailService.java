package com.activityaudit.service;

import com.activityaudit.dto.AuditTrailDTO;
import com.activityaudit.entity.AuditTrail;
import com.activityaudit.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    public Page<AuditTrailDTO> getAuditTrails(Long userId, String actionType, Pageable pageable) {
        Page<AuditTrail> trails;

        if (userId != null) {
            trails = auditTrailRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
        } else if (actionType != null) {
            trails = auditTrailRepository.findByActionTypeOrderByTimestampDesc(actionType, pageable);
        } else {
            trails = auditTrailRepository.findByOrderByTimestampDesc(pageable);
        }

        return trails.map(this::toAuditTrailDTO);
    }

    public AuditTrailDTO getAuditTrailById(Long id) {
        AuditTrail trail = auditTrailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit trail not found"));
        return toAuditTrailDTO(trail);
    }

    private AuditTrailDTO toAuditTrailDTO(AuditTrail trail) {
        return AuditTrailDTO.builder()
                .id(trail.getId())
                .userId(trail.getUser().getId())
                .userName(trail.getUser().getName())
                .actionType(trail.getActionType())
                .entityType(trail.getEntityType())
                .entityId(trail.getEntityId())
                .beforeState(trail.getBeforeState())
                .afterState(trail.getAfterState())
                .changes(trail.getChanges())
                .status(trail.getStatus())
                .timestamp(trail.getTimestamp())
                .build();
    }
}
