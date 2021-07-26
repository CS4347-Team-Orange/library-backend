package edu.utdallas.cs4347.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.ibatis.session.RowBounds;

public class PaginatedController { 

    private static final Logger log = LogManager.getLogger(PaginatedController.class);

    private int limit = 100;

    private int offset = 0;

    public void setLimit(int l) { 
        this.limit = l;
    }

    public int getLimit() { 
        return this.limit;
    }

    public void setOffset(int o) { 
        this.offset = o;
    }

    public int getOffset() { 
        return this.offset;
    }

    public void setByPageNumber(String pageNumber) throws Exception { 
        int pageNum = 1;
    
        try { 
            pageNum = Integer.parseInt(pageNumber);
        } catch (Exception e) { 
            pageNum = 1;
        }

        if (pageNum >= 0) { 
            this.setOffset( this.limit * (pageNum-1));
        } else { 
            throw new Exception("Can't get books, invalid page number");
        }
    }

    public RowBounds getRowBounds() { 
        return new RowBounds(this.getOffset(), this.getLimit());
    }
}
