// File: com/filmus/backend/movie/es/ScoreFunctionFactory.java
package com.filmus.backend.movie.es;

import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ScoreFunctionFactory {

    private final ScoreWeightsProperties w;

    public FunctionScoreQueryBuilder.FilterFunctionBuilder[] buildCommonScoreFuncs() {
        return new FunctionScoreQueryBuilder.FilterFunctionBuilder[] {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("posterUrl"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getPoster())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("stillUrl"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getStill())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("runtime"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getRuntime())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.scriptFunction(
                                new Script(
                                        ScriptType.INLINE,
                                        "painless",
                                        // if no plot → 0, else length>=100 → 1, else 0
                                        "doc['plot'].size()==0 ? 0 : (doc['plot'].value.length() >= 100 ? 1 : 0)",
                                        Collections.emptyMap()
                                )
                        ).setWeight(w.getLongPlot())
                ),

                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("awards1"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getAwards1())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("awards2"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getAwards2())
                ),
                // (b) Actor-rich check
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.scriptFunction(
                                new Script(
                                        ScriptType.INLINE,
                                        "painless",
                                        // if no actorNm → 0, else count>=3 → 1, else 0
                                        "doc['actorNm'].size()==0 ? 0 : (doc['actorNm'].size() >= 3 ? 1 : 0)",
                                        Collections.emptyMap()
                                )
                        ).setWeight(w.getActorRich())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.existsQuery("directorNm"),
                        ScoreFunctionBuilders.weightFactorFunction(w.getDirector())
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        ScoreFunctionBuilders.fieldValueFactorFunction("prodYear")
                                .factor(w.getProdYearFactor())
                                .missing(0)
                )
        };
    }
}