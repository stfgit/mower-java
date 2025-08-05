export interface MowerPosition {
  x: number;
  y: number;
  direction: 'N' | 'E' | 'S' | 'W';
}

export interface MowerCommandRequest {
  lawnDimensions: string;
  mowerPosition: string;
  commands: string;
}

export interface MowerResponse {
  mowerId: string;
  x: number;
  y: number;
  direction: 'N' | 'E' | 'S' | 'W';
  position: string;
}

export interface BatchMowerRequest {
  lawnDimensions: string;
  mowers: Array<{
    mowerPosition: string;
    commands: string;
  }>;
}

export interface LawnDimensions {
  width: number;
  height: number;
}