@import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO
@import com.openelements.maven.initializer.backend.domain.AssertionLibrary
@param ProjectRequestDTO data
@param String className
package ${data.getGroupId()};

import org.junit.jupiter.api.Test;
@if(data.getAssertionLibrary() == AssertionLibrary.ASSERTJ)
import static org.assertj.core.api.Assertions.assertThat;
@elseif(data.getAssertionLibrary() == AssertionLibrary.HAMCREST)
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
@else
import static org.junit.jupiter.api.Assertions.assertTrue;
@endif

class ${className}Test {
    @Test
    void contextLoads() {
@if(data.getAssertionLibrary() == AssertionLibrary.ASSERTJ)
        assertThat(true).isTrue();
@elseif(data.getAssertionLibrary() == AssertionLibrary.HAMCREST)
        assertThat(true, is(true));
@else
        assertTrue(true);
@endif
    }
}

