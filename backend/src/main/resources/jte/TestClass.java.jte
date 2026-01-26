@import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO
@param ProjectRequestDTO data
@param String className
package ${data.getGroupId()};

import org.junit.jupiter.api.Test;
@if("assertj".equals(data.getAssertionLibrary()))
import static org.assertj.core.api.Assertions.assertThat;
@elseif("hamcrest".equals(data.getAssertionLibrary()))
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
@else
import static org.junit.jupiter.api.Assertions.assertTrue;
@endif

class ${className}Test {
    @Test
    void contextLoads() {
@if("assertj".equals(data.getAssertionLibrary()))
        assertThat(true).isTrue();
@elseif("hamcrest".equals(data.getAssertionLibrary()))
        assertThat(true, is(true));
@else
        assertTrue(true);
@endif
    }
}

