package com.triscal.timesheet.infrastructure.persistence;

import com.triscal.timesheet.domain.model.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {

    /**
     * Replica a regra do legado: feriado ativo no dia/mês considerando o tipo e a filial:
     *  LN = local não fixo (ano + filial), NN = nacional não fixo (ano), NF/LF = fixos.
     */
    @Query("""
        select count(f) from Feriado f
        where f.inativo = 'N' and f.dia = :dia and f.mes = :mes and (
              (f.tipo = 'LN' and f.ano = :ano and f.filial = :filial)
           or (f.tipo = 'NN' and f.ano = :ano)
           or (f.tipo in ('NF','LF'))
        )""")
    long contarFeriado(@Param("ano") Short ano, @Param("mes") Short mes,
                       @Param("dia") Short dia, @Param("filial") Integer filial);

    /** Horas a trabalhar do feriado no dia (null se não houver feriado). 0 = folga o dia inteiro. */
    @Query("""
        select min(f.horasATrabalhar) from Feriado f
        where f.inativo = 'N' and f.dia = :dia and f.mes = :mes and (
              (f.tipo = 'LN' and f.ano = :ano and f.filial = :filial)
           or (f.tipo = 'NN' and f.ano = :ano)
           or (f.tipo in ('NF','LF'))
        )""")
    BigDecimal horasFeriado(@Param("ano") Short ano, @Param("mes") Short mes,
                            @Param("dia") Short dia, @Param("filial") Integer filial);
}
